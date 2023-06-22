package com.alwaysrighttempinc.temperaturemeasurmentgenerator.config;


import com.alwaysrighttempinc.temperaturemeasurmentgenerator.model.TemperatureMeasurement;
import com.alwaysrighttempinc.temperaturemeasurmentgenerator.service.TemperatureGeneratorService;
import com.alwaysrighttempinc.temperaturemeasurmentgenerator.service.TemperatureMeasurementPublisher;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Configuration
@EnableScheduling
public class GeneralConfig {
    @Value("${temperature.base}")
    private double baseTemperature;

    @Value("${temperature.generator.batch-size}")
    private double batchSize;

    @Value("${anomaly.temperature-threshold}")
    private double temperatureThreshold;

    @Value("${anomaly.likelihood}")
    private double anomalyLikelihood;

    @Value("${kafka.topic}")
    private String topic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    @Bean
    public Random random() {
        return new Random();
    }

    @Bean
    public TemperatureMeasurementPublisher temperatureMeasurementPublisher() {
        return new TemperatureMeasurementPublisher(topic, kafkaTemplate());
    }

    @Bean
    public TemperatureGeneratorService temperatureGeneratorService() {
        return new TemperatureGeneratorService(baseTemperature, batchSize,
                temperatureThreshold, anomalyLikelihood, random(), temperatureMeasurementPublisher());
    }

    @Bean
    public ProducerFactory<String, TemperatureMeasurement> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
