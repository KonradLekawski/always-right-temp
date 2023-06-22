package com.alwaysrighttempinc.temperaturemeasurmentgenerator.service;

import com.alwaysrighttempinc.temperaturemeasurmentgenerator.model.TemperatureMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;
import java.util.UUID;

public class TemperatureGeneratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureGeneratorService.class);

    private final double baseTemperature;

    private final double batchSize;

    private final double temperatureThreshold;

    private final double anomalyLikelihood;

    private final Random random;

    private final TemperatureMeasurementPublisher temperatureMeasurementPublisher;

    public TemperatureGeneratorService(double baseTemperature,
                                       double batchSize,
                                       double temperatureThreshold,
                                       double anomalyLikelihood,
                                       Random random,
                                       TemperatureMeasurementPublisher temperatureMeasurementPublisher) {
        this.baseTemperature = baseTemperature;
        this.batchSize = batchSize;
        this.temperatureThreshold = temperatureThreshold;
        this.anomalyLikelihood = anomalyLikelihood;
        this.random = random;
        this.temperatureMeasurementPublisher = temperatureMeasurementPublisher;
    }

    @Scheduled(fixedRateString = "${temperature.generator.rate}")
    public void generateAndSendTemperatureData() {
        for (int i = 0; i < batchSize; i++) {
            TemperatureMeasurement measurement = generateTemperatureMeasurement();
            temperatureMeasurementPublisher.publish(measurement);
        }
    }

    private TemperatureMeasurement generateTemperatureMeasurement() {
        double temperature = baseTemperature + random.nextDouble();
        final boolean isAnomaly = random.nextDouble() < anomalyLikelihood;
        if (isAnomaly) {
            temperature += temperatureThreshold + random.nextDouble() * generateAdditionalTemperatureFactor();
        }
        final TemperatureMeasurement measurement = new TemperatureMeasurement(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                System.currentTimeMillis(),
                temperature);
        if (isAnomaly) {
            LOGGER.info("Anomaly generated: {}", measurement);
        }
        return measurement;
    }

    private int generateAdditionalTemperatureFactor() {
        return random.nextInt(1, 30);
    }
}
