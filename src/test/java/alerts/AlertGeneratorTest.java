package alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.util.List;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator alertGenerator;

    @BeforeEach
    void setUp() {
        storage = new DataStorage();
        alertGenerator = new AlertGenerator(storage);
    }

    @Test
    void testCriticalSystolicPressureHigh() {
        storage.addPatientData(1, 190.0, "SystolicPressure", 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Critical Blood Pressure")));
    }

    @Test
    void testCriticalSystolicPressureLow() {
        storage.addPatientData(1, 80.0, "SystolicPressure", 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Critical Blood Pressure")));
    }

    @Test
    void testLowBloodSaturation() {
        storage.addPatientData(1, 88.0, "Saturation", 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Low Blood Saturation")));
    }

    @Test
    void testRapidBloodSaturationDropWithinTenMinutes() {
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 1000L + 9 * 60 * 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Rapid Blood Saturation Drop")));
    }

    @Test
    void testNoRapidBloodSaturationDropOutsideTenMinutes() {
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        storage.addPatientData(1, 93.0, "Saturation", 1000L + 11 * 60 * 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertFalse(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Rapid Blood Saturation Drop")));
    }

    @Test
    void testBloodPressureIncreasingTrend() {
        storage.addPatientData(1, 100.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 130.0, "SystolicPressure", 3000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Blood Pressure Trend")));
    }

    @Test
    void testBloodPressureDecreasingTrend() {
        storage.addPatientData(1, 130.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 115.0, "SystolicPressure", 2000L);
        storage.addPatientData(1, 100.0, "SystolicPressure", 3000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Blood Pressure Trend")));
    }

    @Test
    void testHypotensiveHypoxemia() {
        storage.addPatientData(1, 85.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 88.0, "Saturation", 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Hypotensive Hypoxemia")));
    }

    @Test
    void testTriggeredAlertRecord() {
        storage.addPatientData(1, 1.0, "Alert", 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("Triggered Alert")));
    }

    @Test
    void testEcgPeakDetected() {
        storage.addPatientData(1, 1.0, "ECG", 1000L);
        storage.addPatientData(1, 1.1, "ECG", 2000L);
        storage.addPatientData(1, 0.9, "ECG", 3000L);
        storage.addPatientData(1, 5.0, "ECG", 4000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().contains("ECG Peak Detected")));
    }

    @Test
    void testNoAlertForNormalValues() {
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 80.0, "DiastolicPressure", 1000L);
        storage.addPatientData(1, 98.0, "Saturation", 1000L);
        alertGenerator.evaluateData(storage.getAllPatients().get(0));

        assertTrue(alertGenerator.getTriggeredAlerts().isEmpty());
    }

    @Test
    void testPatientIdInAlert() {
        storage.addPatientData(5, 190.0, "SystolicPressure", 1000L);
        Patient patient = storage.getAllPatients().stream()
                .filter(p -> p.getPatientId() == 5).findFirst().get();
        alertGenerator.evaluateData(patient);

        List<Alert> alerts = alertGenerator.getTriggeredAlerts();
        assertFalse(alerts.isEmpty());
        assertEquals("5", alerts.get(0).getPatientId());
    }
}
