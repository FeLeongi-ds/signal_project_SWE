# Data Storage System

The Data Storage System handles storing, retrieving, and managing patient data from the signal generator. `DataStorage` is the main class and holds all records grouped by patient. Each reading is a `PatientData` object with a patient ID, timestamp, metric type, and value.

`DataRetriever` handles queries from medical staff, letting them get the latest reading or full history for a patient. I added `AccessController` to make sure only authorized roles can access patient data since medical records are sensitive. `DataVersion` keeps track of changes to records over time, which helps with auditing.

The main design choice was keeping storage and retrieval separate. `DataStorage` only writes and manages records while `DataRetriever` only reads them. This way if you change how data is stored it does not break the retrieval side.

For access rules, medical staff can only query data through `DataRetriever` and must pass through `AccessController` first. Direct access to `DataStorage` is not allowed from outside the system. Only internal components like the data generator can write to storage. This separation makes sure patient data is not exposed to anyone who should not have it, which is important in a hospital setting where privacy rules are strict.
