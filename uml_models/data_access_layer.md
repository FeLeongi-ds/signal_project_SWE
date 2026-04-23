# Data Access Layer

The Data Access Layer connects the signal generator to the rest of the system. Since the generator can send data via TCP, WebSocket, or file logs, I used a `DataListener` interface that all three input types share. `TCPDataListener`, `WebSocketDataListener`, and `FileDataListener` all implement this interface, each handling its own connection details.

This means the rest of the system does not need to know how data arrives. You could switch from TCP to WebSocket without changing anything outside this layer. Once data is received it goes to `DataParser`, which detects the format and converts the raw string into a `PatientData` object. `DataSourceAdapter` then takes the parsed data and sends it to storage.

The main goal was flexibility. Each class has one job so adding a new data source just means creating a new listener that implements `DataListener`, nothing else needs to change.

For access rules, external systems like the signal generator only interact with the listener classes. They cannot access `DataParser` or `DataStorage` directly. `DataSourceAdapter` is the only class allowed to forward data to storage, acting as a bridge between the access layer and the rest of the system. This keeps the boundary between external input and internal processing clear and easy to control.
