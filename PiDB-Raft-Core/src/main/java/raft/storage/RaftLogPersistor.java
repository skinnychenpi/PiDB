package raft.storage;

import rpc.RaftProto;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Raft Log Persistor will persist the Raft node log entries on the disk.
 * It's an important infrastructure for log replication.
 * */
public class RaftLogPersistor implements Persistor<RaftProto.Entry> {
    private final String LOG_DIR_PATH;
    private final String FILE_NAME;
    private final RandomAccessFile RAF;
    private long lastLogIndex;
    private long lastLogTerm;

    public RaftLogPersistor(String logDirPath, String fileName) {
        this.LOG_DIR_PATH = logDirPath;
        this.FILE_NAME = fileName;
        this.RAF = RaftFileUtils.openFile(LOG_DIR_PATH, FILE_NAME, "rw");
        this.lastLogIndex = 0L;
        this.lastLogTerm = 0L;
    }

    @Override
    public void persist(List<RaftProto.Entry> logEntries) {
        logEntries.stream().forEach(e -> RaftFileUtils.writeProtoToFile(RAF, e));
    }

    @Override
    public void persist(RaftProto.Entry logEntry) {
        RaftFileUtils.writeProtoToFile(RAF, logEntry);
    }

    @Override
    public List<RaftProto.Entry> read() {
        List<RaftProto.Entry> logEntries = new ArrayList<>();
        while (true) {
            RaftProto.Entry entry = RaftFileUtils.readProtoFromFile(RAF, RaftProto.Entry.class);
            if (entry == null) {
                break;
            } else {
                logEntries.add(entry);
            }
        }
        return logEntries;
    }
    @Override
    public void stop() {
        RaftFileUtils.closeFile(RAF);
    }
}