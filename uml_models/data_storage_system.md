# Data Storage System

The Data Storage System is responsible for securely storing incoming patient measurements from the signal generator and making them available for later retrieval and analysis. `DataStorage` acts as the main storage interface and organizes records by patient ID. It contains multiple `Patient` objects, and each `Patient` contains multiple `PatientRecord` objects. This structure allows the system to retrieve time-stamped medical data for a specific patient and for a selected time range, which supports both real-time monitoring and historical trend analysis.

`PatientRecord` represents one vital-sign measurement at one moment in time, including the patient ID, record type, measurement value, and timestamp. `DataVersion` supports version tracking so that changes to stored data can be recorded with a version ID, change time, and change type. `RetentionPolicy` defines how long records should be kept and whether old records should be archived before deletion.

Data access is handled through `DataRetriever`, which receives requests from `MedicalStaff`. Before returning records, it checks permissions using `AccessController`. This means storage is not exposed directly to every user of the system. Instead, requests pass through a retrieval layer that can enforce access rules before sensitive patient data is returned.

The design separates storage, retrieval, access control, versioning, and deletion policy into different classes. This keeps `DataStorage` focused on managing patient data while support classes handle security and lifecycle rules. The result is a storage subsystem that can support real-time monitoring, historical analysis, controlled access, auditability, and removal of expired records.
