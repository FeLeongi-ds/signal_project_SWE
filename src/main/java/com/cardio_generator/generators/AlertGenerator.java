package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * A {@link PatientDataGenerator} that simulates the triggering and resolution
 * of clinical alerts for a set of patients.
 *
 * <p>Alert generation follows a two-state model:
 * <ul>
 *   <li><strong>No active alert</strong> – on each invocation an alert is
 *       triggered with probability {@code p = 1 − e^{−λ}} (Poisson process
 *       with rate {@code λ = 0.1}).</li>
 *   <li><strong>Alert active</strong> – on each invocation the alert is
 *       resolved with a fixed probability of 90 %.</li>
 * </ul>
 *
 * <p>State transitions are recorded in {@link #alertStates} and the
 * corresponding {@code "Alert"} label ({@code "triggered"} or
 * {@code "resolved"}) is forwarded to the configured
 * {@link OutputStrategy}.
 */
public class AlertGenerator implements PatientDataGenerator {

    /** Shared random number generator used for all probability calculations. */
    public static final Random RANDOM_GENERATOR = new Random();

    /**
     * Per-patient alert state array. {@code false} means no active alert;
     * {@code true} means an alert is currently active. Index 0 is unused; valid
     * indices are 1 through {@code patientCount}.
     */
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Constructs an {@code AlertGenerator} for the given number of patients with
     * all alerts initially in the resolved state.
     *
     * @param patientCount the total number of patients to simulate; patient IDs are
     *                     expected to be in the range [1, patientCount]
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Evaluates the alert state for the specified patient and outputs an event
     * when a state transition occurs.
     *
     * <p>Logic:
     * <ul>
     *   <li>If an alert is already active, it is resolved with 90 % probability
     *       and {@code "Alert"}/{@code "resolved"} is output.</li>
     *   <li>If no alert is active, a new alert is triggered with probability
     *       {@code 1 − e^{−0.1}} and {@code "Alert"}/{@code "triggered"} is
     *       output.</li>
     * </ul>
     *
     * <p>If an unexpected exception occurs, an error message and stack trace are
     * written to {@link System#err} and no output is produced for that call.
     *
     * @param patientId      the unique identifier of the patient; must be in the
     *                       range [1, patientCount] as supplied to the constructor
     * @param outputStrategy the strategy used to record or transmit alert events
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name from Lambda to lambda to follow  naming convention
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}