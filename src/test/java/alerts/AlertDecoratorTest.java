package alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;

class AlertDecoratorTest {

    @Test
    void testPriorityDecoratorAddsPriorityWithoutChangingPatient() {
        Alert base = new Alert("7", "Critical Blood Pressure", 1000L);
        Alert priorityAlert = new PriorityAlertDecorator(base, "HIGH");

        assertEquals("7", priorityAlert.getPatientId());
        assertEquals("HIGH", priorityAlert.getPriority());
        assertEquals("[HIGH] Critical Blood Pressure", priorityAlert.getCondition());
    }

    @Test
    void testRepeatedDecoratorCalculatesNextCheckTime() {
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(
                new Alert("8", "Low Blood Saturation", 5000L), 60000L);

        assertEquals(60000L, repeated.getRepeatIntervalMillis());
        assertEquals(65000L, repeated.getNextCheckTime());
    }
}
