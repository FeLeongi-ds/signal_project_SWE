package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileDataReaderTest {

    @Test
    void testReadNormalValue(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("data.txt"),
                "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 75.5\n");

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(75.5, records.get(0).getMeasurementValue());
        assertEquals("HeartRate", records.get(0).getRecordType());
    }

    @Test
    void testTriggeredAlertMapsToOne(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("alerts.txt"),
                "Patient ID: 2, Timestamp: 2000, Label: Alert, Data: triggered\n");

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(1.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testResolvedAlertMapsToZero(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("alerts.txt"),
                "Patient ID: 2, Timestamp: 3000, Label: Alert, Data: resolved\n");

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(0.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testSaturationStripsPercent(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("sat.txt"),
                "Patient ID: 3, Timestamp: 4000, Label: Saturation, Data: 95%\n");

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(3, 0, Long.MAX_VALUE);
        assertEquals(95.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testMalformedLineIsSkipped(@TempDir Path tempDir) throws IOException {
        Files.writeString(tempDir.resolve("bad.txt"),
                "this is not a valid line\n"
                + "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 60.0\n");

        DataStorage storage = new DataStorage();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
    }
}
