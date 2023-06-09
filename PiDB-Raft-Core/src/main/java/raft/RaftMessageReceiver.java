package raft;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import rpc.RaftProto;
import rpc.RaftRPCGrpc;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RaftMessageReceiver {
    private final int serverId;

    private final int port;

    private final Server gRPCServer;

    private final Object lock;

    private final RaftServer raftServer;

    private static final Logger LOG = LoggerFactory.getLogger(RaftMessageReceiver.class);
    public RaftMessageReceiver(int serverId, int port, RaftServer raftServer) {
        this.serverId = serverId;
        this.port = port;
        this.gRPCServer = ServerBuilder.forPort(port).addService(new RaftService()).build();
        this.lock = new Object();
        this.raftServer = raftServer;
    }

    /**
     * This is a raft core method.
     */
    public void persist() {

    }
    /**
     * This is a raft core method.
     */
    public void readPersist() {

    }


    private long getLastLogIndex() {
        return 0L;
    }


    /**
     * This is a raft core method.
     */
    // the service using Raft (e.g. a k/v server) wants to start
// agreement on the next command to be appended to Raft's log. if this
// server isn't the leader, returns false. otherwise start the
// agreement and return immediately. there is no guarantee that this
// command will ever be committed to the Raft log, since the leader
// may fail or lose an election. even if the Raft instance has been killed,
// this function should return gracefully.
//
// the first return value is the index that the command will appear at
// if it's ever committed. the second return value is the current
// term. the third return value is true if this server believes it is
// the leader.
//
    public boolean startRaft() {
        return true;
    }

    /** Start serving requests. */
    public void start() throws IOException {
        gRPCServer.start();
        LOG.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            try {
                // Class.this is a reference variable that refers to the outer class object
                // inside a nested class or anonymous methods.
                RaftMessageReceiver.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("*** server shut down");
        }));
    }

    /** Stop serving requests and shutdown resources. */
    public void stop() throws InterruptedException {
        if (gRPCServer != null) {
            gRPCServer.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (gRPCServer != null) {
            gRPCServer.awaitTermination();
        }
    }


    // In the future this should be written into the RaftServer start code.

//    public static void main(String[] args) throws Exception {
//        RaftMessageReceiver server = new RaftMessageReceiver(1, 8980);
//        server.start();
//        server.blockUntilShutdown();
//    }





    private class RaftService extends RaftRPCGrpc.RaftRPCImplBase {
        @Override
        public void appendEntries(RaftProto.AppendRequest request, StreamObserver<RaftProto.AppendResponse> responseObserver) {
            responseObserver.onNext(appendEntries(request));
            responseObserver.onCompleted();
        }

        /**
         * This is a dummy method for now.
         */
        private RaftProto.AppendResponse appendEntries(RaftProto.AppendRequest request) {
            RaftProto.AppendResponse.Builder builder = RaftProto.AppendResponse.newBuilder();
            List<RaftProto.Entry> entries = request.getEntriesList();
            int leaderTerm = request.getTerm();
            int currentTerm = raftServer.getCurrentTerm();
            synchronized (lock) {
                if (leaderTerm < currentTerm) {
                    return builder.setSuccess(false).setTerm(currentTerm).build();
                }
            }
            // Currently only heart beat is implemented.
            // If it is a heart beat:
            if (entries.size() == 0) {
                synchronized (lock) {
                    raftServer.setRole(RaftServerRole.FOLLOWER);
                    raftServer.setLeaderID(request.getLeaderID());
                }
            }

            return RaftProto.AppendResponse.newBuilder()
                    .setSuccess(true)
                    .setTerm(currentTerm)
                    .build();
        }

        @Override
        public void requestVote(RaftProto.VoteRequest request, StreamObserver<RaftProto.VoteResponse> responseObserver) {
            responseObserver.onNext(requestVote(request));
            responseObserver.onCompleted();
        }

        /**
        * This is the core logic of requestVote RPC. Please refer to Figure 2 of Raft paper.
        */
        private RaftProto.VoteResponse requestVote(RaftProto.VoteRequest request) {
            RaftProto.VoteResponse.Builder responseBuilder = RaftProto.VoteResponse.newBuilder();
            int candidateTerm = request.getTerm();
            int candidateID = request.getCandidateID();
            int currentTerm = raftServer.getCurrentTerm();
            synchronized (lock) {
                if (candidateTerm >= currentTerm) {
                    int votedFor = raftServer.getVotedFor();
                    // Here we need to lock.
                    if (votedFor == -1) {
                        votedFor = candidateID;
                    }
                    if (votedFor == candidateID) {
                        // grant vote if the candidate's log is at least as up to date as receiver's log
                        // up to date is defined in 5.4.1
                        if (candidateTerm > currentTerm || (candidateTerm == currentTerm && request.getLastLogIndex() >= getLastLogIndex())) {
                            return responseBuilder.setVoteGranted(true).setTerm(currentTerm).build();
                        }
                    }
                }
            }
            return responseBuilder.setVoteGranted(false).setTerm(currentTerm).build();
        }
    }
}
