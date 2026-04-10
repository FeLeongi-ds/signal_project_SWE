package com.cardio_generator.outputs;

/**
 * Strategy interface for delivering simulated patient data to a destination.
 *
 * <p>Implementations define <em>where</em> and <em>how</em> each data point is
 * written (e.g. to the console, a file, a WebSocket connection, or a TCP
 * socket). All implementations must be safe for concurrent use because the
 * scheduler may invoke {@link #output} from multiple threads simultaneously.
 */
public interface OutputStrategy {

    /**
     * Outputs a single patient data point to the underlying destination.
     *
     * @param patientId the unique identifier of the patient this data belongs to
     * @param timestamp the Unix epoch time (in milliseconds) at which the data
     *                  point was generated
     * @param label     a short descriptor identifying the type of measurement
     *                  (e.g. {@code "Saturation"}, {@code "HeartRate"})
     * @param data      the measurement value as a formatted string
     */
    void output(int patientId, long timestamp, String label, String data);
}
