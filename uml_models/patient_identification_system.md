# Patient Identification System

The Patient Identification System makes sure every data point from the simulator is linked to the right patient. `PatientIdentifier` matches an incoming patient ID to a real `HospitalPatient` record and validates the ID before doing anything with it.

`HospitalPatient` holds patient details like name, date of birth, and medical history. These attributes are private so sensitive information is not exposed to parts of the system that do not need it. `IdentityManager` oversees the whole process and handles problems like mismatches or unknown IDs, logging errors when something goes wrong.

`PatientRecord` stores individual data points linked to a patient. I kept it separate from `HospitalPatient` because patient details and measurements are different types of data with different access needs. It also makes it easier to delete old records without touching the patient profile.

For access rules, only `IdentityManager` can register or remove patients. Medical staff can retrieve patient info through `IdentityManager` but cannot directly access `HospitalPatient` or `PatientRecord`. `PatientIdentifier` is only used internally to validate and match IDs, it is not accessible from outside the system. This makes sure patient identity data is always handled in a controlled way and reduces the risk of exposing personal information accidentally.
