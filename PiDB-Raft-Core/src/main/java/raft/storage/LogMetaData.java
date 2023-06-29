package raft.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.RaftProto;

public class LogMetaData {
    private final RaftProto.MetaData metaData;
    private static final Logger LOG = LoggerFactory.getLogger(LogMetaData.class);
    public LogMetaData(int currentTerm, int votedFor) {
        metaData = RaftProto.MetaData.newBuilder()
                .setCurrentTerm(currentTerm)
                .setVotedFor(votedFor)
                .build();
    }

    public RaftProto.MetaData getMetaData() {
        return metaData;
    }
}
