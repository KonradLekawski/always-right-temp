package com.alwaysrighttempinc.anomalydetectionservice.service;

import com.alwaysrighttempinc.anomalydetectionservice.repository.AnomalyRepository;
import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class AnomalyDetectionServiceONE implements AnomalyDetectionService<TemperatureMeasurement> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnomalyDetectionServiceONE.class);
    private static final int MAX_CONSECUTIVE_MEASUREMENTS = 10;

    private final Deque<TemperatureMeasurement> temperatureMeasurements = new ConcurrentLinkedDeque<>();
    private final AtomicLong anomalyCount = new AtomicLong(0);
    private final AtomicLong totalTransactions = new AtomicLong(0);

    private final ReentrantLock lock = new ReentrantLock();

    private final double anomalyThreshold;
    private final AnomalyRepository anomalyRepository;

    public AnomalyDetectionServiceONE(double anomalyThreshold, AnomalyRepository anomalyRepository) {
        if (anomalyRepository == null) {
            throw new IllegalArgumentException("anomalyRepository should not be null");
        }
        this.anomalyThreshold = anomalyThreshold;
        this.anomalyRepository = anomalyRepository;
    }

    @Override
    public void process(Collection<TemperatureMeasurement> newTemperatureMeasurements) {
        newTemperatureMeasurements.forEach(this::processMeasurement);
    }

    private void processMeasurement(TemperatureMeasurement newMeasurement) {
        Collection<TemperatureMeasurement> measurementCopies = enqueueMeasurement(newMeasurement);
        if (measurementCopies.size() != MAX_CONSECUTIVE_MEASUREMENTS) {
            return;
        }
        TemperatureMeasurement anomalyMeasurement = identifyAnomaly(measurementCopies);
        if (anomalyMeasurement != null) {
            saveAnomalyToDatabase(anomalyMeasurement);
        }
        LOGGER.debug("Transaction processed: {}", totalTransactions.incrementAndGet());
    }

    private synchronized Collection<TemperatureMeasurement> enqueueMeasurement(TemperatureMeasurement newMeasurement) {
        lock.lock();
        try {
            if (temperatureMeasurements.size() == MAX_CONSECUTIVE_MEASUREMENTS) {
                temperatureMeasurements.pollFirst();
            }
            temperatureMeasurements.add(newMeasurement);
            return List.copyOf(temperatureMeasurements);
        } finally {
            lock.unlock();
        }
    }

    private TemperatureMeasurement identifyAnomaly(Collection<TemperatureMeasurement> measurementCopies) {
        double temperatureSum = 0.0;
        TemperatureMeasurement maxMeasurement = null;
        for (TemperatureMeasurement measurement : measurementCopies) {
            double currentTemperature = measurement.temperature();
            temperatureSum += currentTemperature;
            if (maxMeasurement == null || currentTemperature > maxMeasurement.temperature()) {
                maxMeasurement = measurement;
            }
        }
        double averageTemperature = temperatureSum / MAX_CONSECUTIVE_MEASUREMENTS;
        if (isAnomaly(maxMeasurement, averageTemperature)) {
            LOGGER.debug("Temperature {} is at least {} greater than average of {}", maxMeasurement.temperature(),
                    anomalyThreshold, averageTemperature);
            LOGGER.warn("Detected an anomaly: {}, total anomalies detected {}", maxMeasurement,
                    anomalyCount.incrementAndGet());
            temperatureMeasurements.removeFirstOccurrence(maxMeasurement);
            return maxMeasurement;
        }
        return null;
    }

    private boolean isAnomaly(TemperatureMeasurement maxMeasurement, double averageTemperature) {
        return maxMeasurement != null && maxMeasurement.temperature() > averageTemperature + anomalyThreshold;
    }

    private void saveAnomalyToDatabase(TemperatureMeasurement maxMeasurement) {
        anomalyRepository.save(new Anomaly(
                UUID.randomUUID().toString(),
                maxMeasurement.measurementId(),
                maxMeasurement.thermometerId(),
                maxMeasurement.roomId(),
                maxMeasurement.timestamp(),
                maxMeasurement.temperature()));
    }
}
