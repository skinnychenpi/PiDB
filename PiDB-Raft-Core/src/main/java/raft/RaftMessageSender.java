package raft;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import rpc.RaftProto;
import rpc.RaftRPCGrpc;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class RaftMessageSender {
    private static final Logger logger = LogManager.getLogger(RaftMessageSender.class.getName());
    private final RaftRPCGrpc.RaftRPCBlockingStub blockingStub;
    private final RaftRPCGrpc.RaftRPCStub asyncStub;

    private final RaftServer raftServer;

    public RaftMessageSender(String host, int port, RaftServer raftServer) {
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forAddress(host, port).usePlaintext();
        Channel channel = managedChannelBuilder.build();
        this.blockingStub = RaftRPCGrpc.newBlockingStub(channel);
        this.asyncStub = RaftRPCGrpc.newStub(channel);
        this.raftServer = raftServer;
    }

    public RaftProto.AppendResponse appendEntries(int term, int leaderId, int prevLogIndex, int prevLogTerm, List<RaftProto.Entry> entries, int leaderCommit) {
        RaftProto.AppendRequest.Builder requestBuilder = RaftProto.AppendRequest.newBuilder()
                .setTerm(term)
                .setPrevLogIndex(prevLogIndex)
                .setLeaderID(leaderId)
                .setPrevLogTerm(prevLogTerm)
                .setLeaderCommit(leaderCommit);
        for (int i = 0; i < entries.size(); i++) {
            requestBuilder.setEntries(i, entries.get(i));
        }
        RaftProto.AppendRequest request = requestBuilder.build();
        RaftProto.AppendResponse response = null;
        try {
            response = blockingStub.appendEntries(request);
            logger.info("Raft Server {} received append entry response: {}", raftServer.getServerId(), response);
            System.out.println(response);
        } catch (Exception e) {
            logger.warn("Raft Server {} RPC failed to append entries.", raftServer.getServerId());
        }
        return response;
    }

    public RaftProto.VoteResponse requestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        RaftProto.VoteRequest request = RaftProto.VoteRequest.newBuilder()
                .setCandidateID(candidateID)
                .setTerm(term)
                .setLastLogIndex(lastLogIndex)
                .setLastLogTerm(lastLogTerm)
                .build();
        RaftProto.VoteResponse response = null;
        try {
            response = blockingStub.requestVote(request);
            logger.info( "Raft Server {} received request vote response: {}", raftServer.getServerId(), response);
            System.out.println(response);
        } catch (Exception e) {
            logger.warn("Raft Server {} RPC failed to request vote.", raftServer.getServerId());
        }
        return response;
    }

//    public static void main(String[] args) {
//        RaftMessageSender client = new RaftMessageSender("localhost", 8980);
//        try {
//            client.requestVote();
//        } catch (Exception e) {
//            System.out.println("Something wrong at Main for the client.");
//        }
//    }


}
