package raft;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import raft.storage.Persistor;
import raft.storage.RaftPersistor;
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

    private int votesReceived;

    private int currentTerm;

    private int votedFor;

    private int commitIndex;

    private int lastApplied;

    private Map<Integer, Integer> nextIndex;

    private Map<Integer, Integer> matchIndex;

    private Timer electionTimer;

    private RaftServerRole role;

    private Persistor persistor;

    private static final Logger LOG = LoggerFactory.getLogger(RaftServer.class);

    private boolean isDead; // A flag for testing. See MIT6.824 go sample code.

    private final Object lock;

    private int leaderID;

    private ScheduledExecutorService scheduledExecutorService;

    private ScheduledFuture electionScheduledFuture;

    public static final int NO_VOTE = -1;

    public RaftServer(int serverID, String host, int port, Map<Integer, RaftServerAddress> serverToAddress) {
        this.serverID = serverID;
        this.port = port;
        this.currentTerm = 0;
        this.votedFor = NO_VOTE;
        this.commitIndex = 0;
        this.lastApplied = 0;
        this.nextIndex = new HashMap<>();
        this.matchIndex = new HashMap<>();
        this.electionTimer = new Timer();
        this.role = RaftServerRole.FOLLOWER;
        this.persistor = new RaftPersistor();
        this.isDead = false;
        this.lock = new Object();
        this.leaderID = -1;

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

    }

    public void start() throws Exception {
        LOG.info("Server {} start...", serverID);
        System.out.println("Server starts.");
        receiver.start();
//        receiver.blockUntilShutdown();
    }

    public synchronized int getCurrentTerm() {
        return currentTerm;
    }

    public synchronized int getCommitIndex() {
        return commitIndex;
    }

    public RaftServerRole getRole() {
        return role;
    }

    public void setRole(RaftServerRole role) {
        this.role = role;
        LOG.info("Server {} set role to {}", serverID, role);
    }

    public synchronized void incrementTerm() {
        this.currentTerm++;
    }

    public synchronized void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public int getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(int votedFor) {
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



     public void onReceiveVoteResponse(RaftProto.VoteResponse response) {
        // TODO: need to change the protobuf file so that when the server sends back a RPC response, the receiver can know who sends this message.
         LOG.info("Server {} receives vote response: term = {}, voteGranted = {}",
                 this.serverID, response.getTerm(), response.getVoteGranted());
         synchronized (lock) {
             if (getRole() == RaftServerRole.CANDIDATE && response.getVoteGranted()) {
                 votesReceived++;
                 // Get elected as a leader, remember to plus one as by default the RaftServer will vote for itself.
                 if (votesReceived + 1 > numOfServers / 2) {
                     votesReceived = 0;
                     setRole(RaftServerRole.LEADER);
                     sendHeartbeats();
                 }
             }
         }
     }

     public void onReceiveHeartbeat(int leaderID) {
        synchronized (lock) {
            setRole(RaftServerRole.FOLLOWER);
            setLeaderID(leaderID);
            // TODO: Need to check the logic of setting the vote as no vote yet.
            //  My logic is that each server will only vote once during each term, so if the term is over,
            //  which means it receives the heartbeat, then the vote should be reset as no vote.
            setVotedFor(NO_VOTE);

            // If the timer is still going, which means the timeout doesn't happen, then reset the timer.
            if (electionScheduledFuture != null && !electionScheduledFuture.isDone()) {
                electionScheduledFuture.cancel(true);
                resetElectionTimer();
            }
        }
     }

     public void sendHeartbeats() {
         // TODO: If when implementing the appendEntries, the logic is also broadcast the RPCs to all other servers,
         //  then we should merge this for loop into the AppendEntries method rather than keep it in this method.
         //  What we need to do is to invoke the appendEntries RPC inside this method
         //  rather than implement the heartbeat logic inside here.
         for (int targetServerID : serverToSender.keySet()) {
             RaftMessageSender sender = serverToSender.get(targetServerID);
             sender.appendEntriesAsync(getCurrentTerm(), getServerId(), -1, -1, new ArrayList<>(), -1);
         }
     }

     public void onReceiveAppendResponse(RaftProto.AppendResponse response) {
         LOG.info("Server {} receives append response: term = {}, appendSuccess = {}",
                 this.serverID, response.getTerm(), response.getSuccess());
     }


     // Should check this method.
     public void resetElectionTimer() {
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
             setRole(RaftServerRole.CANDIDATE);
             incrementTerm();
             setVotedFor(serverID);
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

}
