package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

/**
 * Strategy interface for evaluating patient data against alert rules.
 */
public interface AlertStrategy {

    /**
     * Checks whether the supplied records should trigger alerts.
     *
     * @param patient the patient being evaluated
     * @param records patient records in the evaluation window
     * @return alerts triggered by this strategy
     */
    List<Alert> checkAlert(Patient patient, List<PatientRecord> records);
}
