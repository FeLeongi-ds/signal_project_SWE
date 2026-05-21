package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     * 
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Stops a streaming reader. Batch readers can keep the default no-op behavior.
     *
     * @throws IOException if the reader cannot be stopped cleanly
     */
    default void stopReading() throws IOException {
    }

    /**
     * Indicates whether this reader is currently receiving a live stream.
     *
     * @return true for active streaming readers
     */
    default boolean isStreaming() {
        return false;
    }
}
