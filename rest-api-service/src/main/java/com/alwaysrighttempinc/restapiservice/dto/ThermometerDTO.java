package com.alwaysrighttempinc.restapiservice.dto;


import java.util.List;

public record ThermometerDTO(String thermometerId, List<AnomalyDTO> anomalies) {
}
