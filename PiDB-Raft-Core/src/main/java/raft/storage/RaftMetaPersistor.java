package raft.storage;

import rpc.RaftProto;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Raft Meta Persistor will persist the Raft node log metadata on the disk.
 * It's an important infrastructure for log replication.
 * */
public class RaftMetaPersistor implements Persistor<RaftProto.MetaData> {
    private final String LOG_DIR_PATH;
    private final String FILE_NAME;
    private final RandomAccessFile RAF;

    public RaftMetaPersistor(String logDirPath, String fileName) {
        this.LOG_DIR_PATH = logDirPath;
        this.FILE_NAME = fileName;
        this.RAF = RaftFileUtils.openFile(LOG_DIR_PATH, FILE_NAME, "rw");
    }

    @Override
    public void persist(List<RaftProto.MetaData> metaDataList) {
        metaDataList.stream().forEach(e -> RaftFileUtils.writeProtoToFile(RAF, e));
    }

    @Override
    public void persist(RaftProto.MetaData metaData) {
        RaftFileUtils.overwriteProtoToFile(RAF, metaData);
    }

    @Override
    public List<RaftProto.MetaData> read() {
        List<RaftProto.MetaData> metaDataList = new ArrayList<>();
        while (true) {
            RaftProto.MetaData metaData = RaftFileUtils.readProtoFromFile(RAF, RaftProto.MetaData.class);
            if (metaData == null) {
                break;
            } else {
                metaDataList.add(metaData);
            }
        }
        return metaDataList;
    }

    public int getCurrentTerm() {
        return read().get(0).getCurrentTerm();
    }

    public int getVotedFor() {
        return read().get(0).getVotedFor();
    }

    @Override
    public void stop() {
        RaftFileUtils.closeFile(RAF);
    }
}