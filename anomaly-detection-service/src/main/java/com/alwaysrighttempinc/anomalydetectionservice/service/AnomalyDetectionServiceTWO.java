package com.alwaysrighttempinc.anomalydetectionservice.service;


import com.alwaysrighttempinc.anomalydetectionservice.repository.AnomalyRepository;
import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class AnomalyDetectionServiceTWO implements AnomalyDetectionService<TemperatureMeasurement> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnomalyDetectionServiceTWO.class);
    private final ReentrantLock lock = new ReentrantLock();
    private static final long TIME_WINDOW_IN_SECONDS = 10;

    private final PriorityQueue<TemperatureMeasurement> temperatureMeasurements =
            new PriorityQueue<>(Comparator.comparingLong(TemperatureMeasurement::timestamp));
    private final AtomicLong anomalyCount = new AtomicLong(0);
    private final AtomicLong totalTransactions = new AtomicLong(0);

    private final double anomalyThreshold;
    private final AnomalyRepository anomalyRepository;

    public AnomalyDetectionServiceTWO(double anomalyThreshold, AnomalyRepository anomalyRepository) {
        if(anomalyRepository == null){
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
        PriorityQueue<TemperatureMeasurement> queueCopy = enqueueMeasurement(newMeasurement);
        double averageTemperature = calculateAverageTemperatureInTimeWindow(queueCopy);
        if (isAnomaly(newMeasurement, averageTemperature)) {
            saveAnomalyToDatabase(newMeasurement);
            LOGGER.debug("Temperature {} is at least {} greater than average of {} in 10 second window", newMeasurement.temperature(),
                    anomalyThreshold, averageTemperature);
            LOGGER.warn("Detected an anomaly: {}, total anomalies detected {}", newMeasurement,
                    anomalyCount.incrementAndGet());
        }
        LOGGER.debug("Transaction processed: {}", totalTransactions.incrementAndGet());
    }

    private PriorityQueue<TemperatureMeasurement> enqueueMeasurement(TemperatureMeasurement newMeasurement) {
        lock.lock();
        try {
            while(!temperatureMeasurements.isEmpty() && newMeasurement.timestamp() - temperatureMeasurements.peek().timestamp() > TIME_WINDOW_IN_SECONDS) {
                temperatureMeasurements.poll();
            }
            temperatureMeasurements.add(newMeasurement);
            return new PriorityQueue<>(temperatureMeasurements);
        } finally {
            lock.unlock();
        }
    }

    private double calculateAverageTemperatureInTimeWindow(PriorityQueue<TemperatureMeasurement> queueCopy) {
        return queueCopy.stream()
                .mapToDouble(TemperatureMeasurement::temperature)
                .average()
                .orElse(0.0);
    }

    private boolean isAnomaly(TemperatureMeasurement newMeasurement, double averageTemperature) {
        return newMeasurement.temperature() > averageTemperature + anomalyThreshold;
    }

    private void saveAnomalyToDatabase(TemperatureMeasurement newMeasurement) {
        anomalyRepository.save(new Anomaly(
                UUID.randomUUID().toString(),
                newMeasurement.measurementId(),
                newMeasurement.thermometerId(),
                newMeasurement.roomId(),
                newMeasurement.timestamp(),
                newMeasurement.temperature()));
    }
}