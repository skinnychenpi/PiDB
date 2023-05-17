package raft;

import raft.storage.Persistor;
import raft.storage.RaftPersistor;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

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
    private RaftMessageSender sender;

    private final int serverId;

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

    private static final Logger logger = Logger.getLogger(RaftServer.class.getName());

    private boolean isDead; // A flag for testing. See MIT6.824 go sample code.

    private final Object lock;

    private int leaderID;

    public RaftServer(int serverId, String host, int port) {
        this.serverId = serverId;
        this.port = port;
        this.receiver = new RaftMessageReceiver(serverId, port, this);
        this.sender = new RaftMessageSender(host, port, this);
        this.currentTerm = 0;
        this.votedFor = -1;
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


}
