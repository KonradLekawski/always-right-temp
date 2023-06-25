package com.alwaysrighttempinc.streamapiservice.service;

import com.alwaysrighttempinc.streamapiservice.dto.AnomalyDTO;
import reactor.core.publisher.Flux;


public interface AnomalyService {
    Flux<AnomalyDTO> findLatestDetectedAnomalies();
}
