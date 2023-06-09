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

    public RaftServer(int serverID, String host, int port, Map<Integer, RaftServerAddress> serverToAddress) {
        this.serverID = serverID;
        this.port = port;
        this.currentTerm = 0;
        this.votedFor = -1;
        this.commitIndex = 0;
        this.lastApplied = 0;
        this.nextIndex = new HashMap<>();
        this.matchIndex = new HashMap<>();
        this.electionTimer = new Timer();
        this.role = RaftServerRole.CANDIDATE;
        this.persistor = new RaftPersistor();
        this.isDead = false;
        this.lock = new Object();
        this.leaderID = -1;

        // Start the sender and receiver here.
        /**
         * For the receiver, it's the server of the gRPC service hence for each raftServer entity we only need one receiver.
         * */
        this.receiver = new RaftMessageReceiver(serverID, port, this);
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

    public synchronized void setRole(RaftServerRole role) {
        this.role = role;
    }

    public synchronized void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }

    public synchronized int getVotedFor() {
        return votedFor;
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
            RaftProto.VoteResponse response = sender.requestVote(term, candidateID, lastLogIndex, lastLogTerm);
            LOG.info("Server {} receives message from server {} with contents {}", this.serverID, targetServerID, response);

            // TODO: Only for testing, delete it when logger is feasible.
            System.out.println(targetServerID);
            System.out.println(response);
        }
     }


     // Should check this method.
     public void  startNextVote() {
        electionScheduledFuture = scheduledExecutorService.schedule(() -> {
            requestVote(currentTerm, serverID, 0, 0);
        }, getNextVoteTimer(), TimeUnit.MILLISECONDS);
     }

     private int getNextVoteTimer() {
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
