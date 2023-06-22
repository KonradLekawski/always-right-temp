package com.alwaysrighttempinc.temperaturemeasurmentgenerator.service;

import com.alwaysrighttempinc.temperaturemeasurmentgenerator.model.TemperatureMeasurement;
import org.springframework.kafka.core.KafkaTemplate;

public class TemperatureMeasurementPublisher {

    private final String topic;
    private final KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;

    public TemperatureMeasurementPublisher(String topic, KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(TemperatureMeasurement measurement) {
        kafkaTemplate.send(topic, measurement);
    }
}

