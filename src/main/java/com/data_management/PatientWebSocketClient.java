package com.data_management;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * WebSocket-based data reader that receives real-time simulator messages and
 * stores them in {@link DataStorage}.
 */
public class PatientWebSocketClient extends WebSocketClient implements DataReader {
    private DataStorage dataStorage;
    private PatientDataParser parser;
    private AtomicBoolean running = new AtomicBoolean(false);
    private AtomicInteger malformedMessageCount = new AtomicInteger(0);
    private volatile Exception lastError;

    public PatientWebSocketClient(URI serverUri) {
        this(serverUri, new PatientDataParser());
    }

    public PatientWebSocketClient(URI serverUri, DataStorage dataStorage) {
        this(serverUri, new PatientDataParser());
        this.dataStorage = dataStorage;
    }

    public PatientWebSocketClient(URI serverUri, PatientDataParser parser) {
        super(serverUri);
        this.parser = parser;
    }

    /**
     * Connects to the WebSocket server and starts receiving data asynchronously.
     *
     * @param dataStorage storage updated as new messages arrive
     * @throws IOException if the client cannot connect or is interrupted
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            if (!connectBlocking()) {
                throw new IOException("Could not connect to WebSocket server: " + getURI());
            }
            running.set(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while connecting to WebSocket server", e);
        }
    }

    @Override
    public void stopReading() throws IOException {
        running.set(false);
        try {
            closeBlocking();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while closing WebSocket connection", e);
        }
    }

    @Override
    public boolean isStreaming() {
        return running.get();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        running.set(true);
    }

    @Override
    public void onMessage(String message) {
        processMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        running.set(false);
    }

    @Override
    public void onError(Exception ex) {
        lastError = ex;
        running.set(false);
        System.err.println("WebSocket data reader error: " + ex.getMessage());
    }

    /**
     * Parses and stores one raw WebSocket message. Exposed for unit tests and for
     * separating parsing error handling from network callbacks.
     *
     * @param message raw WebSocket payload
     * @return true when the message was parsed and stored
     */
    public boolean processMessage(String message) {
        if (dataStorage == null) {
            throw new IllegalStateException("DataStorage must be set before processing messages");
        }
        Optional<ParsedPatientData> parsed = parser.parse(message);
        if (parsed.isEmpty()) {
            malformedMessageCount.incrementAndGet();
            System.err.println("Skipping malformed WebSocket message: " + message);
            return false;
        }

        ParsedPatientData data = parsed.get();
        dataStorage.addPatientData(data.getPatientId(), data.getMeasurementValue(),
                data.getRecordType(), data.getTimestamp());
        return true;
    }

    public int getMalformedMessageCount() {
        return malformedMessageCount.get();
    }

    public Exception getLastError() {
        return lastError;
    }
}
