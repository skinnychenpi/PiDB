import raft.RaftServer;
import raft.RaftServerAddress;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

public class RaftCluster {
    public static void main(String[] args) {

        File conf = new File("/Users/chenyuheng/Desktop/PiDB/PiDB-Raft-Core/src/main/conf/log4j.properties");
        PropertyConfigurator.configure(conf.getAbsolutePath());


        Map<Integer, RaftServerAddress> serverAddressMap = new HashMap<>();
        String LOCAL_HOST = "localhost";
        serverAddressMap.put(1, new RaftServerAddress(LOCAL_HOST, 7070));
        serverAddressMap.put(2, new RaftServerAddress(LOCAL_HOST, 7071));
        serverAddressMap.put(3, new RaftServerAddress(LOCAL_HOST, 7072));

        RaftServer server1 = new RaftServer(1, "localhost", 7070, serverAddressMap);
        RaftServer server2 = new RaftServer(2, "localhost", 7071, serverAddressMap);
        RaftServer server3 = new RaftServer(3, "localhost", 7072, serverAddressMap);

//        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("HELLO FROM Meituan!");

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


    }
}
