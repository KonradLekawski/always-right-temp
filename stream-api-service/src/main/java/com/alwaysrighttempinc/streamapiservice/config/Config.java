package com.alwaysrighttempinc.streamapiservice.config;

import com.alwaysrighttempinc.streamapiservice.dto.AnomalyDTO;
import com.alwaysrighttempinc.streamapiservice.service.AnomalyService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
public class Config {

    @Bean
    public RouterFunction<ServerResponse> users(AnomalyService service) {
        return route(GET("/anomalies/stream"),
                req -> ok().contentType(MediaType.TEXT_EVENT_STREAM).body(
                        service.findLatestDetectedAnomalies(), AnomalyDTO.class));
    }

}
