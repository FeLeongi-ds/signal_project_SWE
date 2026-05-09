package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private static final long TEN_MINUTES_IN_MILLIS = 10 * 60 * 1000L;
    private static final double RAPID_SATURATION_DROP_PERCENT = 5.0;

    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.triggeredAlerts = new ArrayList<>();
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        long now = System.currentTimeMillis();
        List<PatientRecord> records = patient.getRecords(0, now);
        checkBloodPressureTrend(patient, records);
        checkCriticalThreshold(patient, records);
        checkBloodSaturation(patient, records);
        checkHypotensiveHypoxemia(patient, records);
        checkEcgPeaks(patient, records);
        checkTriggeredAlerts(patient, records);
    }

    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
        System.out.println("Alert triggered for patient " + alert.getPatientId()
                + ": " + alert.getCondition() + " at " + alert.getTimestamp());
    }

    /**
     * Returns a copy of all alerts that have been triggered so far.
     *
     * @return list of triggered alerts
     */
    public List<Alert> getTriggeredAlerts() {
        return new ArrayList<>(triggeredAlerts);
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        return records.stream()
                .filter(r -> r.getRecordType().equals(type))
                .sorted((a, b) -> Long.compare(a.getTimestamp(), b.getTimestamp()))
                .collect(Collectors.toList());
    }

    private void checkBloodPressureTrend(Patient patient, List<PatientRecord> records) {
        checkTrend(patient, filterByType(records, "SystolicPressure"), "Blood Pressure Trend");
        checkTrend(patient, filterByType(records, "DiastolicPressure"), "Blood Pressure Trend");
    }

    private void checkTrend(Patient patient, List<PatientRecord> typed, String condition) {
        if (typed.size() < 3) {
            return;
        }
        List<PatientRecord> last3 = typed.subList(typed.size() - 3, typed.size());
        double diff1 = last3.get(1).getMeasurementValue() - last3.get(0).getMeasurementValue();
        double diff2 = last3.get(2).getMeasurementValue() - last3.get(1).getMeasurementValue();
        if ((diff1 > 10 && diff2 > 10) || (diff1 < -10 && diff2 < -10)) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    condition, last3.get(2).getTimestamp()));
        }
    }

    private void checkCriticalThreshold(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");
        if (!systolic.isEmpty()) {
            PatientRecord last = systolic.get(systolic.size() - 1);
            double val = last.getMeasurementValue();
            if (val > 180 || val < 90) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Blood Pressure", last.getTimestamp()));
            }
        }
        if (!diastolic.isEmpty()) {
            PatientRecord last = diastolic.get(diastolic.size() - 1);
            double val = last.getMeasurementValue();
            if (val > 120 || val < 60) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Blood Pressure", last.getTimestamp()));
            }
        }
    }

    private void checkBloodSaturation(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> saturation = filterByType(records, "Saturation");
        if (saturation.isEmpty()) {
            return;
        }

        PatientRecord last = saturation.get(saturation.size() - 1);
        if (last.getMeasurementValue() < 92) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Low Blood Saturation", last.getTimestamp()));
        }

        if (hasRapidSaturationDrop(saturation, last)) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Rapid Blood Saturation Drop", last.getTimestamp()));
        }
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

    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> saturation = filterByType(records, "Saturation");
        if (systolic.isEmpty() || saturation.isEmpty()) {
            return;
        }
        double lastSystolic = systolic.get(systolic.size() - 1).getMeasurementValue();
        double lastSat = saturation.get(saturation.size() - 1).getMeasurementValue();
        long ts = Math.max(systolic.get(systolic.size() - 1).getTimestamp(),
                saturation.get(saturation.size() - 1).getTimestamp());
        if (lastSystolic < 90 && lastSat < 92) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Hypotensive Hypoxemia", ts));
        }
    }

    private void checkEcgPeaks(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> ecg = filterByType(records, "ECG");
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
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "ECG Peak Detected", window.get(window.size() - 1).getTimestamp()));
        }
    }

    private void checkTriggeredAlerts(Patient patient, List<PatientRecord> records) {
        List<PatientRecord> alertRecords = filterByType(records, "Alert");
        for (PatientRecord record : alertRecords) {
            if (record.getMeasurementValue() == 1.0) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Triggered Alert", record.getTimestamp()));
            }
        }
    }
}
