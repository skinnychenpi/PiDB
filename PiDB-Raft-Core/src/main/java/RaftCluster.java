import com.google.protobuf.Any;
import raft.RaftServer;
import raft.RaftServerAddress;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;
import raft.storage.LogAction;
import raft.storage.LogEntry;
import raft.storage.RaftFileUtils;
import rpc.RaftProto;

public class RaftCluster {
    public static void main(String[] args) {

        File conf = new File("/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/src/main/conf/log4j.properties");
        PropertyConfigurator.configure(conf.getAbsolutePath());
        String LOG_DIR_PATH = "/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/log";

        Map<Integer, RaftServerAddress> serverAddressMap = new HashMap<>();
        String LOCAL_HOST = "localhost";
        serverAddressMap.put(1, new RaftServerAddress(LOCAL_HOST, 7070));
        serverAddressMap.put(2, new RaftServerAddress(LOCAL_HOST, 7071));
        serverAddressMap.put(3, new RaftServerAddress(LOCAL_HOST, 7072));

        RaftServer server1 = new RaftServer(1, "localhost", 7070, serverAddressMap, LOG_DIR_PATH);
        RaftServer server2 = new RaftServer(2, "localhost", 7071, serverAddressMap, LOG_DIR_PATH);
        RaftServer server3 = new RaftServer(3, "localhost", 7072, serverAddressMap, LOG_DIR_PATH);

//        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            server1.start();
            server2.start();
            server3.start();
        } catch (Exception e) {
            System.out.println("Error at server start...");
            e.printStackTrace();
        }

        try {
            server1.resetElectionTimer();
            server2.resetElectionTimer();
            server3.resetElectionTimer();
        } catch (Exception e) {
            System.out.println("Error at start voting...");
            e.printStackTrace();
        }
        String PATH = "/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/log";
        RandomAccessFile file = RaftFileUtils.openFile(PATH, "S1","rw");
        RaftProto.Entry entry = new LogEntry(LogAction.PUT, "asdf", 1,1).getEntry();
        RaftFileUtils.writeProtoToFile(file, entry);
        RaftFileUtils.closeFile(file);

        RandomAccessFile file1 = RaftFileUtils.openFile(PATH, "S1","r");
        RaftProto.Entry entry1 = RaftFileUtils.readProtoFromFile(file1, RaftProto.Entry.class);
        System.out.println(entry1);
        RaftFileUtils.closeFile(file1);

    }
}
