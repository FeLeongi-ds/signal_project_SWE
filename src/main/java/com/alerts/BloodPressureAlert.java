package com.alerts;

/**
 * Alert type for blood pressure anomalies.
 */
public class BloodPressureAlert extends Alert {

    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
