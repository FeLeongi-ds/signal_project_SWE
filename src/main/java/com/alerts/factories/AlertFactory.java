package com.alerts.factories;

import com.alerts.Alert;

/**
 * Factory Method base class for creating alert objects.
 */
public abstract class AlertFactory {

    /**
     * Creates an alert for the supplied patient and condition.
     *
     * @param patientId the patient identifier
     * @param condition the condition that triggered the alert
     * @param timestamp the time of the triggering measurement
     * @return alert instance
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
