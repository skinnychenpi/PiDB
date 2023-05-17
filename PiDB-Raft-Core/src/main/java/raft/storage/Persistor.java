package raft.storage;

public interface Persistor {
    void persist(byte[] data);

    byte[] read();

    default void persist(String data) {
        persist(data.getBytes());
    }

    default String readAsString() {
        return new String(read());
    }

    default void persist(int data) {
        persist(String.valueOf(data).getBytes());
    }

    default int readAsInt() {
        return Integer.parseInt(readAsString());
    }

    default void persist(long data) {
        persist(String.valueOf(data).getBytes());
    }

    default long readAsLong() {
        return Long.parseLong(readAsString());
    }

    default void persist(float data) {
        persist(String.valueOf(data).getBytes());
    }

    default float readAsFloat() {
        return Float.parseFloat(readAsString());
    }

    default void persist(double data) {
        persist(String.valueOf(data).getBytes());
    }

    default double readAsDouble() {
        return Double.parseDouble(readAsString());
    }

    default void persist(boolean data) {
    }
}
