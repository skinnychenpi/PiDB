package raft;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import rpc.RaftProto;
import rpc.RaftRPCGrpc;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RaftMessageSender {
    private static final Logger LOG = LoggerFactory.getLogger(RaftMessageSender.class);
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
            LOG.info("Raft Server {} received append entry response: {}", raftServer.getServerId(), response);
            System.out.println(response);
        } catch (Exception e) {
            LOG.warn("Raft Server {} RPC failed to append entries.", raftServer.getServerId());
        }
        return response;
    }

    public void appendEntriesAsync(int term, int leaderId, int prevLogIndex, int prevLogTerm, List<RaftProto.Entry> entries, int leaderCommit) {
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
        try {
            LOG.info("Raft Server {} sends append request: term = {}, leaderID = {}, prevLogIndex = {}, prevLogTerm = {}, entries = {}"
                    , raftServer.getServerId(), term, leaderId, prevLogIndex, prevLogTerm, Arrays.toString(entries.toArray()));
            asyncStub.appendEntries(request, new AppendEntryObserver());
        } catch (Exception e) {
            LOG.warn("Raft Server {} RPC failed to append entries.", raftServer.getServerId());
        }
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
            LOG.info("Raft Server {} sends vote request: term = {}, candidateID = {}, lastLogIndex = {}, lastLogTerm = {}",
                    raftServer.getServerId(), term, candidateID, lastLogIndex, lastLogTerm);
            response = blockingStub.requestVote(request);
            asyncStub.requestVote(request,new VoteObserver());
        } catch (Exception e) {
            LOG.warn("Raft Server {} RPC failed to request vote with err: {}", raftServer.getServerId(), e);
        }
        return response;
    }

    public void requestVoteAsync(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        RaftProto.VoteRequest request = RaftProto.VoteRequest.newBuilder()
                .setCandidateID(candidateID)
                .setTerm(term)
                .setLastLogIndex(lastLogIndex)
                .setLastLogTerm(lastLogTerm)
                .build();
        try {
            LOG.info("Raft Server {} sends vote request: term = {}, candidateID = {}, lastLogIndex = {}, lastLogTerm = {}",
                    raftServer.getServerId(), term, candidateID, lastLogIndex, lastLogTerm);
            asyncStub.requestVote(request, new VoteObserver());
        } catch (Exception e) {
            LOG.warn("Raft Server {} RPC failed to request vote with err: {}", raftServer.getServerId(), e);
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

    private class VoteObserver implements StreamObserver<RaftProto.VoteResponse> {
        @Override
        public void onNext(RaftProto.VoteResponse response) {
            raftServer.onSenderReceiveVoteResponse(response);
        }

        @Override
        public void onError(Throwable throwable) {
            LOG.error("Can't get vote response: {} from {}", throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onCompleted() {
//            LOG.info("Raft Server {}'s receiver VoteObserver completed.", raftServer.getServerId());
        }
    }

    private class AppendEntryObserver implements StreamObserver<RaftProto.AppendResponse> {
        @Override
        public void onNext(RaftProto.AppendResponse response) {
            raftServer.onSenderReceiveAppendResponse(response);
        }

        @Override
        public void onError(Throwable throwable) {
            LOG.error("Can't get append response: {} from {}", throwable.getMessage());
            throwable.printStackTrace();
        }

        @Override
        public void onCompleted() {
//            LOG.info("Raft Server {}'s receiver AppendEntryObserver completed.", raftServer.getServerId());
        }
    }

}
