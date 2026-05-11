package com.alerts;

/**
 * Alert type for blood oxygen saturation anomalies.
 */
public class BloodOxygenAlert extends Alert {

    public BloodOxygenAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
