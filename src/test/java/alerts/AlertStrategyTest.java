package alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import java.util.List;

class AlertStrategyTest {

    @Test
    void testBloodPressureStrategyTriggersCriticalAlert() {
        Patient patient = new Patient(1);
        patient.addRecord(190.0, "SystolicPressure", 1000L);
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        AlertStrategy strategy = new BloodPressureStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, records);

        assertTrue(alerts.stream()
                .anyMatch(a -> a.getCondition().equals("High Systolic Blood Pressure")));
    }

    @Test
    void testOxygenSaturationStrategyTriggersLowSaturationAlert() {
        Patient patient = new Patient(2);
        patient.addRecord(88.0, "Saturation", 1000L);
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        AlertStrategy strategy = new OxygenSaturationStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, records);

        assertTrue(alerts.stream()
                .anyMatch(a -> a.getCondition().equals("Low Blood Saturation")));
    }

    @Test
    void testHeartRateStrategyTriggersAbnormalHeartRateAlert() {
        Patient patient = new Patient(3);
        patient.addRecord(140.0, "HeartRate", 1000L);
        List<PatientRecord> records = patient.getRecords(0, Long.MAX_VALUE);

        AlertStrategy strategy = new HeartRateStrategy();
        List<Alert> alerts = strategy.checkAlert(patient, records);

        assertTrue(alerts.stream()
                .anyMatch(a -> a.getCondition().equals("Abnormal Heart Rate")));
    }
}
