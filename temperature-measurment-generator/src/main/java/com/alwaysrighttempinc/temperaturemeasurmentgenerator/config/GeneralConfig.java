package com.alwaysrighttempinc.temperaturemeasurmentgenerator.config;


import com.alwaysrighttempinc.model.TemperatureMeasurement;
import com.alwaysrighttempinc.temperaturemeasurmentgenerator.service.TemperatureGeneratorService;
import com.alwaysrighttempinc.temperaturemeasurmentgenerator.service.TemperatureMeasurementPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Random;

@Configuration
@EnableScheduling
public class GeneralConfig {
    @Value("${temperature.base}")
    private double baseTemperature;

    @Value("${temperature.generator.batch-size}")
    private long batchSize;

    @Value("${anomaly.temperature-threshold}")
    private double temperatureThreshold;

    @Value("${anomaly.likelihood}")
    private double anomalyLikelihood;

    @Value("${spring.kafka.topic.name}")
    private String topic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.partition.number:5}")
    private Integer numberOfPartitions;

    @Value("${spring.kafka.replication.factor:1}")
    private Short replicationFactor;

    private final KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;

    public GeneralConfig(KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public TemperatureMeasurementPublisher temperatureMeasurementPublisher() {
        return new TemperatureMeasurementPublisher(topic, kafkaTemplate);
    }

    @Bean
    public TemperatureGeneratorService temperatureGeneratorService() {
        return new TemperatureGeneratorService(baseTemperature, batchSize,
                temperatureThreshold, anomalyLikelihood, random(), temperatureMeasurementPublisher());
    }

    @Bean
    public NewTopic createNewTopic() {
        return new NewTopic(topic, numberOfPartitions, replicationFactor);
    }

}
