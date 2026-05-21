package integration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.Patient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileMonitoringIntegrationTest {

    @Test
    void testGeneratedFileDataFlowsToStorageAndAlerts(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("SystolicPressure.txt"),
                "Patient ID: 9, Timestamp: 1000, Label: SystolicPressure, Data: 190.0\n"
                + "Patient ID: 9, Timestamp: 2000, Label: SystolicPressure, Data: 120.0\n");
        Files.writeString(tempDir.resolve("Saturation.txt"),
                "Patient ID: 9, Timestamp: 3000, Label: Saturation, Data: 88%\n");

        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        new FileDataReader(tempDir.toString()).readData(storage);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("High Systolic Blood Pressure")));
        assertTrue(alertGenerator.getTriggeredAlerts().stream()
                .anyMatch(a -> a.getCondition().equals("Low Blood Saturation")));
    }
}
