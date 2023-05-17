package raft;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import rpc.RaftProto;
import rpc.RaftRPCGrpc;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RaftMessageSender {
    private static final Logger logger = Logger.getLogger(RaftMessageSender.class.getName());
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

    public void requestVote() {
        RaftProto.VoteRequest request = RaftProto.VoteRequest.newBuilder()
                .setCandidateID(1)
                .setTerm(1)
                .setLastLogIndex(-1)
                .setLastLogIndex(-1)
                .build();
        RaftProto.VoteResponse response;
        try {
            response = blockingStub.requestVote(request);
            logger.log(Level.INFO, "Received response.");
            System.out.println(response);
        } catch (Exception e) {
            logger.log(Level.WARNING, "RPC failed to request vote.");
        }
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
