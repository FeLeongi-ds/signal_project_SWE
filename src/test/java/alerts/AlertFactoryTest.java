package alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;
import com.alerts.BloodPressureAlert;
import com.alerts.ECGAlert;
import com.alerts.factories.BloodOxygenAlertFactory;
import com.alerts.factories.BloodPressureAlertFactory;
import com.alerts.factories.ECGAlertFactory;

class AlertFactoryTest {

    @Test
    void testBloodPressureFactoryCreatesBloodPressureAlert() {
        Alert alert = new BloodPressureAlertFactory()
                .createAlert("1", "Critical Blood Pressure", 1000L);

        assertInstanceOf(BloodPressureAlert.class, alert);
        assertEquals("1", alert.getPatientId());
        assertEquals("Critical Blood Pressure", alert.getCondition());
    }

    @Test
    void testBloodOxygenFactoryCreatesBloodOxygenAlert() {
        Alert alert = new BloodOxygenAlertFactory()
                .createAlert("2", "Low Blood Saturation", 2000L);

        assertInstanceOf(BloodOxygenAlert.class, alert);
        assertEquals(2000L, alert.getTimestamp());
    }

    @Test
    void testEcgFactoryCreatesEcgAlert() {
        Alert alert = new ECGAlertFactory()
                .createAlert("3", "ECG Peak Detected", 3000L);

        assertInstanceOf(ECGAlert.class, alert);
        assertEquals("ECG Peak Detected", alert.getCondition());
    }
}
