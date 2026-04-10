package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * A {@link PatientDataGenerator} that simulates blood oxygen saturation (SpO₂)
 * readings for a set of patients.
 *
 * <p>Each patient is initialised with a baseline SpO₂ value drawn uniformly
 * from the range [95 %, 100 %]. On every subsequent call to
 * {@link #generate(int, OutputStrategy)} the value fluctuates by at most one
 * percentage point and is clamped to [90 %, 100 %] to keep the simulation
 * physiologically plausible.
 *
 * <p>Generated values are forwarded to the provided {@link OutputStrategy}
 * under the label {@code "Saturation"} with a {@code "%"} suffix.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {

    /** Shared random number generator for saturation fluctuations. */
    private static final Random random = new Random();

    /**
     * Per-patient array storing the most recent saturation value. Index 0 is
     * unused; valid indices are 1 through {@code patientCount}.
     */
    private int[] lastSaturationValues;

    /**
     * Constructs a {@code BloodSaturationDataGenerator} for the given number of
     * patients and initialises each patient's saturation to a random baseline in
     * the range [95, 100].
     *
     * @param patientCount the total number of patients to simulate; patient IDs are
     *                     expected to be in the range [1, patientCount]
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a simulated blood oxygen saturation reading for the specified
     * patient and outputs it via the given strategy.
     *
     * <p>The new value is the previous value adjusted by a random delta of
     * {@code -1}, {@code 0}, or {@code +1} percentage points, then clamped to the
     * physiologically safe range [90, 100]. The updated value is stored so that
     * successive readings follow a realistic trajectory.
     *
     * <p>If an unexpected exception occurs, an error message and stack trace are
     * written to {@link System#err} and no output is produced for that call.
     *
     * @param patientId      the unique identifier of the patient; must be in the
     *                       range [1, patientCount] as supplied to the constructor
     * @param outputStrategy the strategy used to record or transmit the generated
     *                       saturation value
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
