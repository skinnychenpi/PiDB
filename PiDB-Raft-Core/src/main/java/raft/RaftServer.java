package raft;

import raft.storage.Persistor;
import raft.storage.RaftPersistor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(RaftServer.class.getName());

    private boolean isDead; // A flag for testing. See MIT6.824 go sample code.

    private final Object lock;

    private int leaderID;

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

     public void requestVote(int serverID, int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        RaftMessageSender sender = serverToSender.get(serverID);
        RaftProto.VoteResponse response = sender.requestVote(term, candidateID, lastLogIndex, lastLogTerm);
     }

}
