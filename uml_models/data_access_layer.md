# Data Access Layer

The Data Access Layer is responsible for receiving raw data from the external signal generator and converting it into a format that the rest of the CHMS can use. The system may receive data through several input sources, including TCP, WebSocket, and file-based logs. To keep these input methods consistent, `DataListener` is modeled as an interface that defines the common operations for listening and receiving data.

`TCPDataListener`, `WebSocketDataListener`, and `FileDataListener` implement the `DataListener` interface. Each listener handles a specific input source but follows the same contract, which allows the rest of the system to remain independent from the source of the data. After raw data is received, the concrete listener passes it to `DataSourceAdapter`.

`DataSourceAdapter` coordinates the conversion process. It uses `DataParser` to validate and parse raw input into structured patient data, then sends the parsed result to `DataStorage`. This separates external communication, parsing logic, and storage responsibilities. The storage system does not need to know whether the data originally came from a socket, a WebSocket stream, or a file.

This design improves modularity and extensibility. If the signal generator later supports another input method, the system can add a new listener class that implements `DataListener` without changing `DataParser` or `DataStorage`. It also keeps parsing rules centralized, which reduces duplicated format-handling logic. The layer therefore acts as a boundary between external data sources and the internal patient monitoring system.
