package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.ECGAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy for heart rate and ECG rhythm alerts.
 */
public class HeartRateStrategy implements AlertStrategy {
    private AlertFactory alertFactory = new ECGAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        addHeartRateAlert(patient, filterByType(records, "HeartRate"), alerts);
        addEcgPeakAlert(patient, filterByType(records, "ECG"), alerts);
        return alerts;
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        return records.stream()
                .filter(r -> r.getRecordType().equals(type))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }

    private void addHeartRateAlert(Patient patient, List<PatientRecord> heartRate, List<Alert> alerts) {
        if (heartRate.isEmpty()) {
            return;
        }
        PatientRecord last = heartRate.get(heartRate.size() - 1);
        double value = last.getMeasurementValue();
        if (value > 130 || value < 40) {
            alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                    "Abnormal Heart Rate", last.getTimestamp()));
        }
    }

    private void addEcgPeakAlert(Patient patient, List<PatientRecord> ecg, List<Alert> alerts) {
        if (ecg.size() < 2) {
            return;
        }
        int windowSize = Math.min(30, ecg.size());
        List<PatientRecord> window = ecg.subList(ecg.size() - windowSize, ecg.size());
        double sum = 0;
        for (int i = 0; i < window.size() - 1; i++) {
            sum += Math.abs(window.get(i).getMeasurementValue());
        }
        double mean = sum / (window.size() - 1);
        double latest = Math.abs(window.get(window.size() - 1).getMeasurementValue());
        if (mean > 0 && latest > 2 * mean) {
            alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                    "ECG Peak Detected", window.get(window.size() - 1).getTimestamp()));
        }
    }
}
