package com.alwaysrighttempinc.restapiservice.config;

import com.alwaysrighttempinc.restapiservice.repository.AnomalyRepository;
import com.alwaysrighttempinc.restapiservice.service.AnomalyService;
import com.alwaysrighttempinc.restapiservice.service.BasicAnomalyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${anomaly.count.threshold:2}")
    private Integer anomalyCountThreshold;

    private final AnomalyRepository anomalyRepository;

    public Config(AnomalyRepository anomalyRepository) {
        this.anomalyRepository = anomalyRepository;
    }

    @Bean
    public AnomalyService anomalyService() {
        return new BasicAnomalyService(anomalyRepository, anomalyCountThreshold);
    }
}
