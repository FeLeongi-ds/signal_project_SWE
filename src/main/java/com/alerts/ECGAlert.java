package com.alerts;

/**
 * Alert type for ECG and heart rhythm anomalies.
 */
public class ECGAlert extends Alert {

    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
