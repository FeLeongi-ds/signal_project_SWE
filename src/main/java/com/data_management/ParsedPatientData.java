package com.data_management;

/**
 * Immutable representation of a parsed patient data message.
 */
public class ParsedPatientData {
    private int patientId;
    private long timestamp;
    private String recordType;
    private double measurementValue;

    public ParsedPatientData(int patientId, long timestamp, String recordType, double measurementValue) {
        this.patientId = patientId;
        this.timestamp = timestamp;
        this.recordType = recordType;
        this.measurementValue = measurementValue;
    }

    public int getPatientId() {
        return patientId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getRecordType() {
        return recordType;
    }

    public double getMeasurementValue() {
        return measurementValue;
    }
}
