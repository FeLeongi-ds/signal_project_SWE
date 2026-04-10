package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * An {@link OutputStrategy} that streams patient data over a plain TCP socket
 * to a single connected client.
 *
 * <p>A {@link ServerSocket} is opened on the specified port during construction.
 * Client acceptance is performed asynchronously on a dedicated single-thread
 * executor so that the simulator's main thread is not blocked while waiting for
 * a connection. Once a client connects, all subsequent {@link #output} calls
 * send comma-separated records to that client in the format:
 * <pre>&lt;patientId&gt;,&lt;timestamp&gt;,&lt;label&gt;,&lt;data&gt;</pre>
 *
 * <p>Only one client at a time is supported. A second connection attempt will
 * not be accepted until the server is restarted.
 */
public class TcpOutputStrategy implements OutputStrategy {

    /** Server socket that listens for incoming client connections. */
    private ServerSocket serverSocket;

    /** The currently connected client socket, or {@code null} before a client connects. */
    private Socket clientSocket;

    /** Writer backed by the client socket's output stream; {@code null} until connected. */
    private PrintWriter out;

    /**
     * Constructs a {@code TcpOutputStrategy} that listens on the given port.
     *
     * <p>The server socket is created immediately and begins listening. Client
     * acceptance runs in a background thread, so the constructor returns without
     * blocking. Any {@link IOException} during socket creation or client acceptance
     * is printed to the standard error stream.
     *
     * @param port the TCP port number on which to listen for client connections
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a single patient data record to the connected TCP client.
     *
     * <p>The record is formatted as a comma-separated line:
     * <pre>&lt;patientId&gt;,&lt;timestamp&gt;,&lt;label&gt;,&lt;data&gt;</pre>
     *
     * <p>If no client has connected yet (i.e. {@code out} is {@code null}), the
     * call is silently ignored.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the Unix epoch time (in milliseconds) of the measurement
     * @param label     the measurement type label (e.g. {@code "HeartRate"})
     * @param data      the measurement value as a formatted string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
