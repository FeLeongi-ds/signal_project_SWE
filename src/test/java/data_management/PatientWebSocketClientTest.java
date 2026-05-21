package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.PatientWebSocketClient;
import java.net.URI;
import java.util.List;

class PatientWebSocketClientTest {
    private DataStorage storage;
    private PatientWebSocketClient client;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        storage.clear();
        client = new PatientWebSocketClient(URI.create("ws://localhost:9999"), storage);
    }

    @Test
    void testProcessMessageStoresParsedData() {
        assertTrue(client.processMessage("Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 80.0"));

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals("HeartRate", records.get(0).getRecordType());
        assertEquals(80.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testProcessMessageSkipsCorruptedData() {
        assertFalse(client.processMessage("corrupted-message"));

        assertEquals(1, client.getMalformedMessageCount());
        assertTrue(storage.getAllPatients().isEmpty());
    }

    @Test
    void testDuplicateRealtimeMessageIsNotStoredTwice() {
        String message = "2,2000,Saturation,91.0";

        assertTrue(client.processMessage(message));
        assertTrue(client.processMessage(message));

        assertEquals(1, storage.getRecords(2, 0, Long.MAX_VALUE).size());
    }

    @Test
    void testOnCloseMarksStreamAsStopped() {
        client.onOpen(null);
        assertTrue(client.isStreaming());

        client.onClose(1000, "normal", false);

        assertFalse(client.isStreaming());
    }
}
