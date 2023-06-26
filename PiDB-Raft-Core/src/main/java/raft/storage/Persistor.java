package raft.storage;

import com.google.protobuf.Message;
import rpc.RaftProto;

import java.util.List;

public interface Persistor<T extends Message>  {
    void persist(List<T> entries);

    void persist(T entry);

    List<T> read();
}
