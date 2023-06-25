package com.alwaysrighttempinc.streamapiservice.dto;

public record AnomalyDTO(String anomalyId, String thermometerId, String roomId, Long timestamp, Double temperature, Double temperatureDifference) {

}
