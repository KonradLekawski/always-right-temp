package com.alwaysrighttempinc.streamapiservice.api;

import com.alwaysrighttempinc.streamapiservice.config.Config;
import com.alwaysrighttempinc.streamapiservice.dto.AnomalyDTO;
import com.alwaysrighttempinc.streamapiservice.service.AnomalyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@WebFluxTest
@ContextConfiguration(classes = {Config.class})
class ApiTest {
    @Autowired
    protected WebTestClient webTestClient;

    @MockBean
    protected AnomalyService anomalyService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(anomalyService);
    }

    @Test
    void shouldReturnStreamApiWithLatestAnomalies() {
        //given
        when(anomalyService.findLatestDetectedAnomalies()).thenReturn(Flux.just(new AnomalyDTO(
                "anomalyId",
                "themometerId",
                "roomId",
                10000L,
                30.0,
                10.0
        )));

        //when
        FluxExchangeResult<AnomalyDTO> result = webTestClient.get().uri("/anomalies/stream")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(AnomalyDTO.class);

        //then
        StepVerifier.create(result.getResponseBody())
                .expectNextMatches(anomalyDTO -> {
                    return anomalyDTO.anomalyId().equals("anomalyId") &&
                            anomalyDTO.thermometerId().equals("themometerId") &&
                            anomalyDTO.roomId().equals("roomId") &&
                            anomalyDTO.timestamp().equals(10000L) &&
                            anomalyDTO.temperature() == 30.0 &&
                            anomalyDTO.temperatureDifference() == 10.0;
                })
                .verifyComplete();
    }
}
