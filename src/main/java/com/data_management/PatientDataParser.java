package com.data_management;

import java.util.Optional;

/**
 * Parses patient data messages produced by the simulator output strategies.
 */
public class PatientDataParser {

    /**
     * Parses either the file-style format or the compact WebSocket format.
     *
     * <p>Supported examples:
     * <pre>
     * Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 75.5
     * 1,1000,HeartRate,75.5
     * </pre>
     *
     * @param message raw message from a file or WebSocket stream
     * @return parsed data, or empty when the message is malformed
     */
    public Optional<ParsedPatientData> parse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return Optional.empty();
        }

        try {
            String trimmed = message.trim();
            if (trimmed.startsWith("Patient ID:")) {
                return Optional.of(parseNamedFormat(trimmed));
            }
            return Optional.of(parseCompactFormat(trimmed));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private ParsedPatientData parseNamedFormat(String message) {
        String[] parts = message.split(", ");
        int patientId = Integer.parseInt(parts[0].split(": ")[1].trim());
        long timestamp = Long.parseLong(parts[1].split(": ")[1].trim());
        String label = parts[2].split(": ")[1].trim();
        String data = parts[3].split(": ")[1].trim();
        return new ParsedPatientData(patientId, timestamp, label, parseMeasurementValue(data));
    }

    private ParsedPatientData parseCompactFormat(String message) {
        String[] parts = message.split(",", 4);
        if (parts.length != 4) {
            throw new IllegalArgumentException("Expected four comma-separated fields");
        }
        int patientId = Integer.parseInt(parts[0].trim());
        long timestamp = Long.parseLong(parts[1].trim());
        String label = parts[2].trim();
        String data = parts[3].trim();
        return new ParsedPatientData(patientId, timestamp, label, parseMeasurementValue(data));
    }

    private double parseMeasurementValue(String data) {
        if (data.equals("triggered")) {
            return 1.0;
        }
        if (data.equals("resolved")) {
            return 0.0;
        }
        return Double.parseDouble(data.replace("%", ""));
    }
}
