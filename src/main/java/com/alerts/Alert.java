package com.alerts;

/**
 * Represents an alert raised by the monitoring system.
 */
public class Alert {
    private String patientId;
    private String condition;
    private long timestamp;

    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the alert priority. Plain alerts use normal priority; decorators can
     * override this value dynamically.
     *
     * @return alert priority label
     */
    public String getPriority() {
        return "NORMAL";
    }
}
