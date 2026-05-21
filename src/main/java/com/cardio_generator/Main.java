package com.cardio_generator;

import com.alerts.AlertGenerator;
import com.data_management.DataReader;
import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientWebSocketClient;
import com.data_management.Patient;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Entry point for the Cardiovascular Health Monitoring System project.
 * Dispatches to the simulator, the storage demo, or the file-based monitoring
 * flow depending on the first command-line argument.
 */
public class Main {

    /**
     * Starts the requested application mode.
     *
     * <p>Supported modes:
     * <ul>
     *   <li>{@code DataStorage}: runs the DataStorage demonstration main method.</li>
     *   <li>{@code monitor [directory]}: reads generated file output and evaluates alerts.</li>
     *   <li>{@code monitor-websocket <ws://host:port>}: receives live WebSocket data.</li>
     *   <li>any other arguments: forwarded to {@link HealthDataSimulator}.</li>
     * </ul>
     *
     * @param args command-line arguments selecting the application mode
     * @throws IOException if the data directory cannot be read
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("DataStorage")) {
            DataStorage.main(Arrays.copyOfRange(args, 1, args.length));
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("monitor")) {
            String dataDir = args.length > 1 ? args[1] : "output";
            runMonitoring(dataDir);
            return;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("monitor-websocket")) {
            String uri = args.length > 1 ? args[1] : "ws://localhost:8080";
            runWebSocketMonitoring(uri);
            return;
        }

        HealthDataSimulator.main(args);
    }

    private static void runMonitoring(String dataDir) throws IOException {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        DataReader reader = new FileDataReader(dataDir);
        reader.readData(storage);

        AlertGenerator alertGenerator = new AlertGenerator(storage);
        for (Patient patient : storage.getAllPatients()) {
            alertGenerator.evaluateData(patient);
        }

        System.out.println("Monitoring complete. Evaluated "
                + storage.getAllPatients().size() + " patient(s).");
    }

    private static void runWebSocketMonitoring(String websocketUri) throws IOException {
        DataStorage storage = DataStorage.getInstance();
        storage.clear();
        PatientWebSocketClient reader = new PatientWebSocketClient(URI.create(websocketUri));
        reader.readData(storage);
        System.out.println("Connected to " + websocketUri + ". Press Ctrl+C to stop monitoring.");
    }
}
