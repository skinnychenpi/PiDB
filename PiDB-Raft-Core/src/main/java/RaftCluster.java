import raft.RaftServer;
import raft.RaftServerAddress;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import org.apache.log4j.PropertyConfigurator;

public class RaftCluster {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // TODO: For LOG_DIR_PATH and Conf path should be store in a config file, this is only for current development.
        File conf = new File("/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/src/main/conf/log4j.properties");
        PropertyConfigurator.configure(conf.getAbsolutePath());
        String LOG_DIR_PATH = "/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/log";

        Map<Integer, RaftServerAddress> serverAddressMap = new HashMap<>();
        String LOCAL_HOST = "localhost";
        serverAddressMap.put(1, new RaftServerAddress(LOCAL_HOST, 7070));
        serverAddressMap.put(2, new RaftServerAddress(LOCAL_HOST, 7071));
        serverAddressMap.put(3, new RaftServerAddress(LOCAL_HOST, 7072));

        RaftServer server1 = new RaftServer(1, LOCAL_HOST, 7070, serverAddressMap, LOG_DIR_PATH);
        RaftServer server2 = new RaftServer(2, LOCAL_HOST, 7071, serverAddressMap, LOG_DIR_PATH);
        RaftServer server3 = new RaftServer(3, LOCAL_HOST, 7072, serverAddressMap, LOG_DIR_PATH);

        Executor workers = Executors.newFixedThreadPool(3);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        workers.execute(() -> {
            try {
                server1.start();
            } catch (Exception e) {
                System.out.println("Error at server1 start...");
                e.printStackTrace();
            }
        });

        workers.execute(() -> {
            try {
                server2.start();
            } catch (Exception e) {
                System.out.println("Error at server1 start...");
                e.printStackTrace();
            }
        });

        workers.execute(() -> {
            try {
                server3.start();
            } catch (Exception e) {
                System.out.println("Error at server1 start...");
                e.printStackTrace();
            }
        });

        ScheduledFuture future = scheduledExecutorService.schedule(() -> {
            try {
                server1.stop();
                server2.stop();
                server3.stop();
            } catch (Exception e) {
                System.out.println("Errors occur when trying to stop the server.");
            }
        }, 5000, TimeUnit.MILLISECONDS);

        future.get();
        System.exit(0);

        // Experiment for log storage.
//        String PATH = "/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/log";
//        RandomAccessFile file = RaftFileUtils.openFile(PATH, "S1","rw");
//        RaftProto.Entry entry = new LogEntry(LogAction.PUT, "asdf", 1,1).getEntry();
//        RaftFileUtils.writeProtoToFile(file, entry);
//        RaftFileUtils.closeFile(file);
//
//        RandomAccessFile file1 = RaftFileUtils.openFile(PATH, "S1","r");
//        RaftProto.Entry entry1 = RaftFileUtils.readProtoFromFile(file1, RaftProto.Entry.class);
//        System.out.println(entry1);
//        RaftFileUtils.closeFile(file1);

    }
}
