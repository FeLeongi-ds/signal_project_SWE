package com.alerts.strategies;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy for blood pressure trend and threshold alerts.
 */
public class BloodPressureStrategy implements AlertStrategy {
    private AlertFactory alertFactory = new BloodPressureAlertFactory();

    @Override
    public List<Alert> checkAlert(Patient patient, List<PatientRecord> records) {
        List<Alert> alerts = new ArrayList<>();
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");

        addTrendAlert(patient, systolic, alerts);
        addTrendAlert(patient, diastolic, alerts);
        addThresholdAlerts(patient, systolic, 90, 180, "Systolic", alerts);
        addThresholdAlerts(patient, diastolic, 60, 120, "Diastolic", alerts);
        return alerts;
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        return records.stream()
                .filter(r -> r.getRecordType().equals(type))
                .sorted(Comparator.comparingLong(PatientRecord::getTimestamp))
                .collect(Collectors.toList());
    }

    private void addTrendAlert(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        if (records.size() < 3) {
            return;
        }
        List<PatientRecord> last3 = records.subList(records.size() - 3, records.size());
        double diff1 = last3.get(1).getMeasurementValue() - last3.get(0).getMeasurementValue();
        double diff2 = last3.get(2).getMeasurementValue() - last3.get(1).getMeasurementValue();
        if ((diff1 > 10 && diff2 > 10) || (diff1 < -10 && diff2 < -10)) {
            alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                    "Blood Pressure Trend", last3.get(2).getTimestamp()));
        }
    }

    private void addThresholdAlerts(Patient patient, List<PatientRecord> records, double low,
            double high, String pressureType, List<Alert> alerts) {
        for (PatientRecord record : records) {
            double value = record.getMeasurementValue();
            if (value > high) {
                alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                        "High " + pressureType + " Blood Pressure", record.getTimestamp()));
            } else if (value < low) {
                alerts.add(alertFactory.createAlert(String.valueOf(patient.getPatientId()),
                        "Low " + pressureType + " Blood Pressure", record.getTimestamp()));
            }
        }
    }
}
