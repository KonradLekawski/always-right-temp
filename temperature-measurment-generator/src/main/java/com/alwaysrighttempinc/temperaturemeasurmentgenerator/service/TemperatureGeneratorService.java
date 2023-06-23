package com.alwaysrighttempinc.temperaturemeasurmentgenerator.service;

import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class TemperatureGeneratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureGeneratorService.class);
    public static final long AMOUNT_OF_THERMOMETERS = 100L;
    public static final long AMOUNT_OF_ROOMS = 100L;

    private final double baseTemperature;

    private final long batchSize;

    private final double temperatureThreshold;

    private final double anomalyLikelihood;

    private final Random random;

    private final TemperatureMeasurementPublisher temperatureMeasurementPublisher;

    private final AtomicLong anomaliesProduced = new AtomicLong(0);

    public TemperatureGeneratorService(double baseTemperature,
                                       long batchSize,
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
        Stream.generate(this::generateTemperatureMeasurement)
                .parallel()
                .limit(batchSize)
                .forEach(temperatureMeasurementPublisher::publish);
    }

    private TemperatureMeasurement generateTemperatureMeasurement() {
        double temperature = baseTemperature + random.nextDouble();
        final boolean isAnomaly = random.nextDouble() < anomalyLikelihood;
        if (isAnomaly) {
            temperature += temperatureThreshold + random.nextDouble() * generateAdditionalTemperatureFactor();
        }
        final TemperatureMeasurement measurement = new TemperatureMeasurement(
                UUID.randomUUID().toString(),
                generateThermometerId(),
                generateRoomId(),
                System.currentTimeMillis(),
                temperature);
        if (isAnomaly) {
            LOGGER.info("Anomaly generated: {} TOTAL anomalies produced {}", measurement, anomaliesProduced.incrementAndGet());
        }
        return measurement;
    }

    private String generateThermometerId() {
        return "Thermometer-" + random.nextLong(AMOUNT_OF_THERMOMETERS);
    }

    private String generateRoomId() {
        return "Room-" + random.nextLong(AMOUNT_OF_ROOMS);
    }

    private int generateAdditionalTemperatureFactor() {
        return random.nextInt(1, 30);
    }
}
