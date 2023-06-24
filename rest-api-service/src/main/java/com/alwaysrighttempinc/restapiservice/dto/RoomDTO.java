package com.alwaysrighttempinc.restapiservice.dto;


import java.util.List;

public record RoomDTO(String roomId, List<AnomalyDTO> anomalies) {
}
