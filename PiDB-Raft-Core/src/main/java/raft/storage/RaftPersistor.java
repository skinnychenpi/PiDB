package raft.storage;

public class RaftPersistor implements Persistor{
    private int currentTerm;
    private int votedFor;
    private LogEntry[] logEntries;

    @Override
    public void persist(byte[] data) {

    }

    @Override
    public byte[] read() {
        return new byte[0];
    }
}