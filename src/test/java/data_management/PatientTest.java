package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

class PatientTest {

    @Test
    void testGetPatientId() {
        Patient patient = new Patient(42);
        assertEquals(42, patient.getPatientId());
    }

    @Test
    void testAddAndRetrieveRecord() {
        Patient patient = new Patient(1);
        patient.addRecord(75.0, "HeartRate", 1000L);
        patient.addRecord(80.0, "HeartRate", 2000L);

        List<PatientRecord> records = patient.getRecords(500L, 2500L);
        assertEquals(2, records.size());
        assertEquals(75.0, records.get(0).getMeasurementValue());
        assertEquals(80.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testGetRecordsFiltersByTimeRange() {
        Patient patient = new Patient(1);
        patient.addRecord(75.0, "HeartRate", 1000L);
        patient.addRecord(80.0, "HeartRate", 3000L);

        List<PatientRecord> records = patient.getRecords(500L, 2000L);
        assertEquals(1, records.size());
        assertEquals(75.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsReturnsEmptyWhenNoneMatch() {
        Patient patient = new Patient(1);
        patient.addRecord(75.0, "HeartRate", 5000L);

        List<PatientRecord> records = patient.getRecords(0L, 1000L);
        assertTrue(records.isEmpty());
    }
}
