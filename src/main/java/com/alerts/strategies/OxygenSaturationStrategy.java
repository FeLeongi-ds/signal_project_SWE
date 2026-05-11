package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy for oxygen saturation alerts.
 */
public class OxygenSaturationStrategy implements AlertStrategy {
    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L;
    private static final double RAPID_SATURATION_DROP_PERCENT = 5.0;

    private AlertFactory alertFactory = new BloodOxygenAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> saturation = filterByType(records, "Saturation");
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        if (saturation.isEmpty()) {
            return alerts;
        }

        PatientRecord last = saturation.get(saturation.size() - 1);
        if (last.getMeasurementValue() < 92) {
            alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                    "Low Blood Saturation", last.getTimestamp()));
        }
        if (hasRapidSaturationDrop(saturation, last)) {
            alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                    "Rapid Blood Saturation Drop", last.getTimestamp()));
        }
        addHypotensiveHypoxemiaAlert(patient, systolic, saturation, alerts);
        return alerts;
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        return records.stream()
                .filter(r -> r.getRecordType().equals(type))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }

    private boolean hasRapidSaturationDrop(List<PatientRecord> saturation, PatientRecord latest) {
        for (int i = saturation.size() - 2; i >= 0; i--) {
            PatientRecord previous = saturation.get(i);
            long elapsed = latest.getTimestamp() - previous.getTimestamp();
            if (elapsed > TEN_MINUTES_IN_MILLIS) {
                break;
            }
            double drop = previous.getMeasurementValue() - latest.getMeasurementValue();
            if (drop >= RAPID_SATURATION_DROP_PERCENT) {
                return true;
            }
        }
        return false;
    }

    private void addHypotensiveHypoxemiaAlert(Patient patient, List<PatientRecord> systolic,
            List<PatientRecord> saturation, List<Alert> alerts) {
        if (systolic.isEmpty() || saturation.isEmpty()) {
            return;
        }
        PatientRecord lastSystolic = systolic.get(systolic.size() - 1);
        PatientRecord lastSaturation = saturation.get(saturation.size() - 1);
        if (lastSystolic.getMeasurementValue() < 90 && lastSaturation.getMeasurementValue() < 92) {
            alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                    "Hypotensive Hypoxemia",
                    Math.max(lastSystolic.getTimestamp(), lastSaturation.getTimestamp())));
        }
    }
}
