package com.cardio_generator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class HealthDataSimulatorTest {

    @Test
    void testGetInstanceReturnsSameSimulator() {
        assertSame(HealthDataSimulator.getInstance(), HealthDataSimulator.getInstance());
    }
}
