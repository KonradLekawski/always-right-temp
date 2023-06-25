package com.alwaysrighttempinc.streamapiservice.service;

import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.streamapiservice.dto.AnomalyDTO;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;


@Component
public class AnomalyStreamService implements AnomalyService {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public AnomalyStreamService(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public Flux<AnomalyDTO> findLatestDetectedAnomalies() {
        Flux<ChangeStreamEvent<Anomaly>> streamEventFlux = reactiveMongoTemplate.changeStream(Anomaly.class)
                .listen();
        return streamEventFlux
                .map(ChangeStreamEvent::getBody)
                .map(anomaly -> new AnomalyDTO(
                        anomaly.anomalyId(),
                        anomaly.thermometerId(),
                        anomaly.roomId(),
                        anomaly.timestamp(),
                        anomaly.temperature(),
                        anomaly.temperatureDifference()));
    }
}
