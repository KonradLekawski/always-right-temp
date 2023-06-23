package com.alwaysrighttempinc.anomalydetectionservice.service;

import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaTemperatureMeasurementListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTemperatureMeasurementListener.class);
    private final AnomalyDetectionService<TemperatureMeasurement> anomalyDetectionService;

    public KafkaTemperatureMeasurementListener(AnomalyDetectionService<TemperatureMeasurement> anomalyDetectionService) {
        this.anomalyDetectionService = anomalyDetectionService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name}",
            concurrency = "${spring.kafka.consumer.level.concurrency:3}")
    public void consume(List<TemperatureMeasurement> batch) {
        LOGGER.debug("Consumed batch with {} records", batch.size());
        anomalyDetectionService.process(batch);
    }
}
