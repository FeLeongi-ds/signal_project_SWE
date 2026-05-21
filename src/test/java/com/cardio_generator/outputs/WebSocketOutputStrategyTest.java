package com.cardio_generator.outputs;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class WebSocketOutputStrategyTest {

    @Test
    void testFormatMessageContainsAllPatientFields() {
        String message = WebSocketOutputStrategy.formatMessage(1, 1000L, "HeartRate", "75.0");

        assertEquals("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 75.0", message);
    }
}
