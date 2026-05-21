package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.ParsedPatientData;
import com.data_management.PatientDataParser;
import java.util.Optional;

class PatientDataParserTest {

    @Test
    void testParsesNamedMessageFormat() {
        PatientDataParser parser = new PatientDataParser();

        Optional<ParsedPatientData> data = parser.parse(
                "Patient ID: 1, Timestamp: 1000, Label: HeartRate, Data: 75.5");

        assertTrue(data.isPresent());
        assertEquals(1, data.get().getPatientId());
        assertEquals(1000L, data.get().getTimestamp());
        assertEquals("HeartRate", data.get().getRecordType());
        assertEquals(75.5, data.get().getMeasurementValue());
    }

    @Test
    void testParsesCompactWebSocketFormat() {
        PatientDataParser parser = new PatientDataParser();

        Optional<ParsedPatientData> data = parser.parse("2,2000,Saturation,95%");

        assertTrue(data.isPresent());
        assertEquals(2, data.get().getPatientId());
        assertEquals("Saturation", data.get().getRecordType());
        assertEquals(95.0, data.get().getMeasurementValue());
    }

    @Test
    void testMapsTriggeredAndResolvedAlerts() {
        PatientDataParser parser = new PatientDataParser();

        assertEquals(1.0, parser.parse("3,3000,Alert,triggered").get().getMeasurementValue());
        assertEquals(0.0, parser.parse("3,4000,Alert,resolved").get().getMeasurementValue());
    }

    @Test
    void testMalformedMessageReturnsEmpty() {
        PatientDataParser parser = new PatientDataParser();

        assertTrue(parser.parse("bad-message").isEmpty());
        assertTrue(parser.parse("").isEmpty());
    }
}
