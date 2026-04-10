package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An {@link OutputStrategy} that appends each patient data point to a
 * label-specific text file inside a configurable base directory.
 *
 * <p>One file per measurement label is created (e.g. {@code Saturation.txt},
 * {@code HeartRate.txt}). The mapping from label to file path is cached in
 * {@link #fileMap} so that the path is computed only on the first write for
 * each label. The base directory is created automatically if it does not yet
 * exist.
 *
 * <p>This class is thread-safe: {@link #fileMap} uses a
 * {@link ConcurrentHashMap} and individual writes are synchronised through the
 * append-mode {@link java.io.PrintWriter}.
 */
public class FileOutputStrategy implements OutputStrategy {

    /** Root directory under which all output files are created. */
    private String baseDirectory;

    /**
     * Cache that maps each measurement label to the absolute path of its
     * corresponding output file.
     */
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a {@code FileOutputStrategy} that writes data files into the
     * specified base directory.
     *
     * @param baseDirectory the path to the root output directory; the directory is
     *                      created by {@link #output} if it does not already exist
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * Appends a formatted patient data record to the file corresponding to
     * {@code label}. The file is created if it does not yet exist; otherwise the
     * record is appended to the end.
     *
     * <p>Each record is written in the format:
     * <pre>Patient ID: &lt;id&gt;, Timestamp: &lt;ts&gt;, Label: &lt;label&gt;, Data: &lt;data&gt;</pre>
     *
     * <p>If the base directory cannot be created, or if an I/O error occurs while
     * writing, an error message is printed to {@link System#err} and the method
     * returns without throwing.
     *
     * @param patientId the unique identifier of the patient
     * @param timestamp the Unix epoch time (in milliseconds) of the measurement
     * @param label     the measurement type, used as the output file name stem
     * @param data      the measurement value as a formatted string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the FilePath variable
        // Changed variable name from FilePath to filePath to follow naming convention
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}