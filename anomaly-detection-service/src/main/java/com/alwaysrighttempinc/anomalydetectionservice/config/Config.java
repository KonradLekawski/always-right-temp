package com.alwaysrighttempinc.anomalydetectionservice.config;

import com.alwaysrighttempinc.anomalydetectionservice.repository.AnomalyRepository;
import com.alwaysrighttempinc.anomalydetectionservice.service.AnomalyDetectionService;
import com.alwaysrighttempinc.anomalydetectionservice.service.AnomalyDetectionServiceONE;
import com.alwaysrighttempinc.anomalydetectionservice.service.AnomalyDetectionServiceTWO;
import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${detection.anomaly.threshold:5}")
    private int anomalyThreshold;

    private final AnomalyRepository anomalyRepository;

    public Config(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    @Bean
    @ConditionalOnProperty(
            value="detection.anomaly.algorithm",
            havingValue = "ONE",
            matchIfMissing = true)
    AnomalyDetectionService<TemperatureMeasurement> anomalyDetectionServiceONE() {
        return new AnomalyDetectionServiceONE(anomalyThreshold, anomalyRepository);
    }

    @Bean
    @ConditionalOnProperty(
            value="detection.anomaly.algorithm",
            havingValue = "TWO")
    AnomalyDetectionService<TemperatureMeasurement> anomalyDetectionServiceTWO() {
        return new AnomalyDetectionServiceTWO(anomalyThreshold, anomalyRepository);
    }
}
