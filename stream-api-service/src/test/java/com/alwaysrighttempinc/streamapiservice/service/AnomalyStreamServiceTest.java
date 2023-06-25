package com.alwaysrighttempinc.streamapiservice.service;

import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.streamapiservice.dto.AnomalyDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveChangeStreamOperation;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnomalyStreamServiceTest {

    private ReactiveMongoTemplate reactiveMongoTemplate;
    private AnomalyStreamService anomalyStreamService;

    @BeforeEach
    public void setUp() {
        reactiveMongoTemplate = mock(ReactiveMongoTemplate.class);
        anomalyStreamService = new AnomalyStreamService(reactiveMongoTemplate);
    }

    @Test
    void shouldReturnFluxOfAnomalyDTOsWhenFindLatestDetectedAnomaliesIsCalled() {
        //given
        Anomaly anomaly = new Anomaly("anomalyId", "measurementOne", "thermometerOne",
                "roomOne", 10000L, 25.0, 5.0 );

        ChangeStreamEvent<Anomaly> changeStreamEventMock = mock(ChangeStreamEvent.class);
        ReactiveChangeStreamOperation.ReactiveChangeStream<Anomaly> reactiveChangeStream =
                mock(ReactiveChangeStreamOperation.ReactiveChangeStream.class);

        when(reactiveMongoTemplate.changeStream(Anomaly.class)).thenReturn(reactiveChangeStream);
        when(reactiveChangeStream.listen()).thenReturn(Flux.just(changeStreamEventMock));
        when(changeStreamEventMock.getBody()).thenReturn(anomaly);

        //when
        Flux<AnomalyDTO> resultFlux = anomalyStreamService.findLatestDetectedAnomalies();

        //then
        StepVerifier.create(resultFlux)
                .expectNextMatches(anomalyDTO -> {
                    return anomalyDTO.anomalyId().equals("anomalyId") &&
                            anomalyDTO.thermometerId().equals("thermometerOne") &&
                            anomalyDTO.roomId().equals("roomOne") &&
                            anomalyDTO.timestamp().equals(10000L) &&
                            anomalyDTO.temperature() == 25.0 &&
                            anomalyDTO.temperatureDifference() == 5.0;
                })
                .verifyComplete();
    }
}
