package raft.storage;

import com.google.protobuf.Any;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raft.RaftMessageReceiver;
import rpc.RaftProto;

public class LogEntry {
     private final RaftProto.Entry entry;
     private static final Logger LOG = LoggerFactory.getLogger(LogEntry.class);
     public LogEntry(LogAction action, Object key, Object value, int term) {
         switch (action) {
             case GET:
                 entry = RaftProto.Entry.newBuilder().setAction(RaftProto.Action.GET).setKey((Any) key).setTerm(term).build();
                 break;
             case PUT:
                 entry = RaftProto.Entry.newBuilder().setAction(RaftProto.Action.PUT).setKey((Any) key).setValue((Any) value).setTerm(term).build();
                 break;
             default:
                 entry = null;
                 LOG.error("Only GET or PUT action for LogEntry.");
         }
     }

     public RaftProto.Entry getEntry() {
         return entry;
     }
}
