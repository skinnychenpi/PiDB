package raft;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raft.storage.LogEntry;
import raft.storage.LogMetaData;
import raft.storage.RaftLogPersistor;
import raft.storage.RaftMetaPersistor;
import rpc.RaftProto;

/**
 * This is a class that represents a Raft Server Node, which is a server that runs the raft services.
 * It should be regarded as an instance that can be deployed on a physical machine.
 **/
public class RaftServer {
    /**
     * In order to cope with the grpc framework, here for each RaftServer, it has a RaftMessageSender and a RaftMessageReceiver.
     * The RaftMessageSender is responsible for sending out the RPC to other RaftServer and collects the responses.
     * The RaftMessageReceiver is responsible for receiving the RPC and sending back the processed results.
     **/
    private RaftMessageReceiver receiver;
    private Map<Integer, RaftMessageSender> serverToSender;

    private final int serverID;

    private final int port;

    private final int numOfServers;
    /**
     * A counter that counts how many votes a candidate receives during election for each term.
     * Note that it will be reset to 0 only when the server is turn into a new candidate.
     * */
    private int votesReceived;

    private int currentTerm;
    /**
     * Candidate server ID that received the vote from this server in the current term.
     * Note that every term one vote. If term increases, votes will be reset as NO_VOTE.
     * */
    private int votedFor;

    private int commitIndex;

    private int lastApplied;

    private List<RaftProto.Entry> logEntries;

    private Map<Integer, Integer> nextIndex;

    private Map<Integer, Integer> matchIndex;

    private Timer electionTimer;

    private RaftServerRole role;

    private final RaftLogPersistor raftLogPersistor;

    private final RaftMetaPersistor raftLogMetaPersistor;

    private static final Logger LOG = LoggerFactory.getLogger(RaftServer.class);

    private boolean isDead; // A flag for testing. See MIT6.824 go sample code.

    private final Object lock;

    private int leaderID;

    private ScheduledExecutorService scheduledExecutorService;

    private ScheduledFuture electionScheduledFuture;

    public static final int NO_VOTE = -1;

    public static final int NO_LEADER = -1;

    private final String LOG_DIR_PATH;

    private final String ENTRY_LOG_FILE_NAME;

    private final String LOG_META_FILE_NAME;

    //TODO: Not quite sure log index is necessary or not !!!!!!!!
    private int logIndex;

    public RaftServer(int serverID, String host, int port, Map<Integer, RaftServerAddress> serverToAddress, String logDirPath) {
        this.serverID = serverID;
        this.port = port;
        this.currentTerm = 0;
        this.votedFor = NO_VOTE;
        this.commitIndex = 0;
        this.lastApplied = 0;
        this.nextIndex = new HashMap<>();;
        this.matchIndex = new HashMap<>();;
        this.electionTimer = new Timer();
        this.role = RaftServerRole.FOLLOWER;
        this.isDead = false;
        this.lock = new Object();
        this.leaderID = NO_LEADER;

        // Start the sender and receiver here.
        /**
         * For the receiver, it's the server of the gRPC service hence for each raftServer entity we only need one receiver.
         * */
        this.receiver = new RaftMessageReceiver(serverID, port, this, lock);
        /**
         * For the sender, for each raftServer, we need to send the RPC to all other raft servers.
         * Hence, we need to construct multiple sender. The number of sender is equal to the number of all other raft servers.
         * */
        this.serverToSender = new HashMap<>();
        for (int serverId : serverToAddress.keySet()) {
            // Only connect with other servers, hence only construct senders to other servers.
            if (serverId != serverID) {
                String senderHost = serverToAddress.get(serverId).getHost();
                int senderPort = serverToAddress.get(serverId).getPort();
                serverToSender.put(serverId, new RaftMessageSender(senderHost, senderPort, this));
            }
        }

        numOfServers = serverToAddress.size();
        votesReceived = 0;

        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        electionScheduledFuture = null;

        LOG_DIR_PATH = logDirPath;
        ENTRY_LOG_FILE_NAME = "Server" + serverID + "_Data";
        LOG_META_FILE_NAME = "Server" + serverID + "_Meta";
        this.raftLogPersistor = new RaftLogPersistor(LOG_DIR_PATH, ENTRY_LOG_FILE_NAME);
        this.raftLogMetaPersistor = new RaftMetaPersistor(LOG_DIR_PATH, LOG_META_FILE_NAME);

        this.logEntries = new ArrayList<>();
        this.logIndex = 1; // Index start from 1
    }

    public void start() throws Exception {
        LOG.info("Server {} start...", serverID);
        // Recover log entries
        LOG.info("Server {} recovering log...", serverID);
        logEntries = raftLogPersistor.read();
        LOG.info("Server {} recovering log done", serverID);
        // Recover log metadata
        LOG.info("Server {} recovering log meta...", serverID);
        loadLogMeta();
        LOG.info("Server {} recovering log meta done", serverID);

        // Start Server and reset timer for next election.
        receiver.start();
        resetElectionTimer();
        LOG.info("Server {} is now running.", serverID);
//        receiver.blockUntilShutdown();
    }

    private void loadLogMeta() {
        // Load commit Index from log entries
        for (int i = logEntries.size(); i >= 1; i--) {
            RaftProto.Entry entry = logEntries.get(i - 1);
            if (entry.getIsCommitted() && i > commitIndex) {
                commitIndex = i;
                break;
            }
        }
        // Load Last Applied from log entries
        lastApplied = Math.max(lastApplied, logEntries.size());
        currentTerm = raftLogMetaPersistor.getCurrentTerm();
        votedFor = raftLogMetaPersistor.getVotedFor();
        logIndex = logEntries.size();
    }

    public void stop() throws Exception {
        // Stop the timer
        if (electionScheduledFuture != null) {
            electionScheduledFuture.cancel(true);
        }
        receiver.stop();
        LOG.info("Server {} stops.", serverID);
    }

    /**
     * This method is added to the shutdown hook. On server exit, the metadata will automatically write into the file.
     * */
    public void persistOnServerStop() {
        synchronized (lock) {
            // Persist the log entries for future recovery.
            // TODO: In the future, the log entries might be persisted on alteration, rather than persist all on exit.
            //  Need to think about whether it is necessary and feasible or not.
            raftLogPersistor.persist(logEntries);

            // Persist the log metadata for future recovery.
            raftLogMetaPersistor.persist(new LogMetaData(currentTerm, votedFor).getMetaData());
            raftLogMetaPersistor.stop();
            raftLogPersistor.stop();
        }
        LOG.info("Server {} persist data done.", serverID);
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public void setCurrentTerm(int currentTerm) {
        this.currentTerm = currentTerm;
    }

    public int getCommitIndex() {
        return commitIndex;
    }

    public void setCommitIndex(int commitIndex) {
        this.commitIndex = commitIndex;
    }

    public int getLastApplied() {
        return lastApplied;
    }

    public RaftServerRole getRole() {
        return role;
    }

    public void setRole(RaftServerRole role) {
        this.role = role;
        LOG.info("Server {} set role to {} at term {}", serverID, role, currentTerm);
    }

    public void incrementTerm() {
        this.currentTerm++;
    }

    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public int getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(int votedFor) {
        LOG.debug("Server {} vote for Server {} as leader at term {}", serverID, votedFor, currentTerm);
        this.votedFor = votedFor;
    }
    public int getServerId() {
        return serverID;
    }

    public void appendEntries(int serverID, int term, int leaderId, int prevLogIndex, int prevLogTerm, List<RaftProto.Entry> entries, int leaderCommit) {
        // This code is for testing....
        RaftMessageSender sender = serverToSender.get(serverID);
        RaftProto.AppendResponse response = sender.appendEntries(term, leaderId, prevLogIndex, prevLogTerm, entries, leaderCommit);
    }


     private void requestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        for (int targetServerID : serverToSender.keySet()) {
            // TODO: Here we need to change the logic into async such as using completableFuture rather than using sync like this.
            // TODO: This is only for testing......
            RaftMessageSender sender = serverToSender.get(targetServerID);
//            RaftProto.VoteResponse response = sender.requestVote(term, candidateID, lastLogIndex, lastLogTerm);
//            LOG.info("Server {} receives vote response from server {}: term = {}, voteGranted = {}",
//                    this.serverID, targetServerID, response.getTerm(), response.getVoteGranted());

            sender.requestVoteAsync(term, candidateID, lastLogIndex, lastLogTerm);
        }
     }



     public void onSenderReceiveVoteResponse(RaftProto.VoteResponse response) {
        // TODO: need to change the protobuf file so that when the server sends back a RPC response, the receiver can know who sends this message.
         LOG.info("Server {} receives vote response: term = {}, voteGranted = {}",
                 serverID, response.getTerm(), response.getVoteGranted());
         synchronized (lock) {
             if (response.getTerm() > currentTerm) {
                 currentTerm = response.getTerm();
                 setRole(RaftServerRole.FOLLOWER);
             }
             if (getRole() == RaftServerRole.CANDIDATE) {
                 if (response.getVoteGranted()) {
                     votesReceived++;
                     // Get elected as a leader, remember to plus one as by default the RaftServer will vote for itself.
                     if (votesReceived + 1 > numOfServers / 2) {
                         onGetElectedAsLeader();
                     }
                 }
             } else {
                 LOG.info("Server {} is not a candidate but a {}.", serverID, getRole());
             }
         }
     }

     private void onGetElectedAsLeader() {
         setRole(RaftServerRole.LEADER);
         for (int serverID : serverToSender.keySet()) {
             nextIndex.put(serverID, logIndex + 1); // TODO: Need to check logIndex logic
             matchIndex.put(serverID, 0);
         }
         sendHeartbeats();
     }

     public void sendHeartbeats() {
         // TODO: If when implementing the appendEntries, the logic is also broadcast the RPCs to all other servers,
         //  then we should merge this for loop into the AppendEntries method rather than keep it in this method.
         //  What we need to do is to invoke the appendEntries RPC inside this method
         //  rather than implement the heartbeat logic inside here.
         for (int targetServerID : serverToSender.keySet()) {
             RaftMessageSender sender = serverToSender.get(targetServerID);
             RaftProto.Entry lastLog = getLastLog();
             sender.appendEntriesAsync(getCurrentTerm(), getServerId(), lastLog == null ? -1 : lastLog.getIndex(), lastLog == null ? -1 : lastLog.getTerm(), new ArrayList<>(), -1);
         }
     }

     public void onSenderReceiveAppendResponse(RaftProto.AppendResponse response) {
         LOG.info("Server {} sender receives append response: term = {}, appendSuccess = {}",
                 this.serverID, response.getTerm(), response.getSuccess());
         synchronized (lock) {
             if (response.getTerm() > currentTerm) {
                 currentTerm = response.getTerm();
                 setRole(RaftServerRole.FOLLOWER);
             }
         }
     }


     /**
      * Based on Figure 2 Rules for Servers(Followers):
      * If election timeout elapses without receiving AppendEntries RPC from current leader or granting vote to candidate: convert to candidate.
      * We can know that upon:
      * a) you get an AppendEntries RPC from the current leader (i.e., if the term in the AppendEntries arguments is outdated, you should not reset your timer);
      * b) you are starting an election;
      * or c) you grant a vote to another peer.
      * */
     public void resetElectionTimer() {
         // If the timer is still going, which means the timeout doesn't happen, then reset the timer.
         if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
             electionScheduledFuture.cancel(true);
         }
         electionScheduledFuture = scheduledExecutorService.schedule(this::beginElection, getTimeForNextElection(), TimeUnit.MILLISECONDS);
     }

    /**
     * Based on Raft paper Section 5.2, the Leader Election follows the below procedure.
     * Step 1: The server increment its current term and transit to candidate state.
     * Step 2: Votes for itself and sends RequestVote RPCs to other servers.
     * Step 3: Based on the RPCs received, judge whether it wins the election. If yes, sends heartbeat. If no, wait for heartbeat.
     * */
     private void beginElection() {
         synchronized (lock) {
             incrementTerm();
             setRole(RaftServerRole.CANDIDATE);
             setVotedFor(serverID);
             votesReceived = 0;
         }
         resetElectionTimer();
         requestVote(currentTerm, serverID, 0, 0);
     }

     private int getTimeForNextElection() {
         // Here we assume the period to start a vote is generated randomly from a fixed interval.
         // TODO: In the future, the lower bound and upper bound should be retrieved from the config file.
         int lowerBound = 500;
         int upperBound = 1000;
         double period = lowerBound + (upperBound - lowerBound) * Math.random();
         int periodInInt = (int) period;
         LOG.info("The vote timer is set as {}ms for server {}", periodInInt, serverID);
         return (int) period;
     }

    public boolean onReceiverReceiveAppendRequest(int prevLogIndex, int prevLogTerm, List<RaftProto.Entry> leaderSentNewEntries, int leaderCommit) {
        // Receiver implementation 2: Reply false if log doesn't contain an entry at prevLogIndex whose term matches prevLogTerm.
        if (prevLogIndex > logEntries.size()) {
            return false;
        }
        RaftProto.Entry entryAtPrevLogIndex = logEntries.get(prevLogIndex - 1); // Minus one because log index starts from 1.
        if (entryAtPrevLogIndex.getTerm() != prevLogTerm) {
            return false;
        }
        // Receiver implementation 3, 4: If an existing entry conflicts with a new one, delete the existing entry and all that follow it.
        // Append new entries if not in the log.
        for (int i = 0; i < leaderSentNewEntries.size(); i++) {
            RaftProto.Entry leaderEntry = leaderSentNewEntries.get(i);
            int followerLogIndex = prevLogIndex + 1 + i;
            if (followerLogIndex < logEntries.size()) {
                RaftProto.Entry followerEntry = logEntries.get(followerLogIndex);
                if (!followerEntry.equals(leaderEntry)) {
                    logEntries.set(followerLogIndex, leaderEntry);
                }
            } else {
                logEntries.add(leaderEntry);
            }
        }

        // Receiver implementation 5: Please refer to Raft paper.
        if (leaderCommit > getCommitIndex()) {
            setCommitIndex(Math.min(leaderCommit, logEntries.size())); // TODO: Check the logic here. Also I use logEntries.size() because I set the start of the index as 1 but not 0.
        }

        return true;
    }

    public RaftProto.Entry getLastLog() {
         return logEntries.isEmpty() ? null : logEntries.get(logEntries.size() - 1);
    }
}
