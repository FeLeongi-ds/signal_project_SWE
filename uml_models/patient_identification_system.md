# Patient Identification System

The Patient Identification System ensures that each incoming data point from the signal generator is linked to the correct hospital patient before it is stored or analyzed. `IncomingPatientData` represents unverified measurements that arrive with a patient ID, record type, measurement value, and timestamp. `IdentityManager` coordinates the process by receiving incoming data and asking `PatientIdentifier` to validate the patient ID.

`PatientIdentifier` checks the incoming patient ID against `PatientRegistry`, which maintains the known `HospitalPatient` records. Each `HospitalPatient` stores identifying information such as hospital ID, name, date of birth, and medical history. This allows the system to retrieve patient information and confirm that the incoming measurement belongs to a valid patient.

If the patient ID is valid, the data can continue to the rest of the monitoring system. If no match is found or the data appears suspicious, `IdentityManager` creates a `MismatchRecord` describing the failed match and the reason for the anomaly. This is important because incorrect patient matching could cause medical data to be stored under the wrong patient, which would be unsafe and difficult to audit later.

`IdentificationLog` records identification attempts, including successful and failed checks, so the system remains traceable. The log supports later review by showing which patient ID was processed, when it was processed, and whether the identification succeeded. This design separates incoming data, patient matching, registry lookup, and mismatch handling into focused classes. It also makes the subsystem easier to extend if future versions need stronger identity checks, duplicate detection, or manual review workflows.
