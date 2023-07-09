package raft.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.RaftProto;

public class LogEntry {
     private final RaftProto.Entry entry;
     private static final Logger LOG = LoggerFactory.getLogger(LogEntry.class);
     public LogEntry(CommandAction action, String key, int value, int term, int index) {
         switch (action) {
             case GET:
                 RaftProto.Command getCommand = RaftProto.Command.newBuilder().setAction(RaftProto.Action.GET).setKey(key).build();
                 entry = RaftProto.Entry.newBuilder().setCommand(getCommand).setTerm(term).setIndex(index).build();
                 break;
             case PUT:
                 RaftProto.Command putCommand = RaftProto.Command.newBuilder().setAction(RaftProto.Action.PUT).setKey(key).setValue(value).build();
                 entry = RaftProto.Entry.newBuilder().setCommand(putCommand).setTerm(term).setIndex(index).build();
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
