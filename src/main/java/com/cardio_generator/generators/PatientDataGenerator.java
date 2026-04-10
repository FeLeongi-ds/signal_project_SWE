package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Contract for classes that generate simulated health data for a single patient
 * and deliver it to an {@link com.cardio_generator.outputs.OutputStrategy}.
 *
 * <p>Implementations are expected to be stateful (e.g. they may retain the
 * previous measurement so that successive values follow a realistic trajectory)
 * and should be safe to call from multiple threads as long as each patient ID
 * is owned by exactly one calling thread.
 */
public interface PatientDataGenerator {

    /**
     * Generates one data point for the specified patient and forwards it to the
     * given output strategy.
     *
     * @param patientId      the unique identifier of the patient for whom data is
     *                       being generated; must be a positive integer within the
     *                       range this generator was initialised for
     * @param outputStrategy the strategy used to persist or transmit the generated
     *                       data point
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
