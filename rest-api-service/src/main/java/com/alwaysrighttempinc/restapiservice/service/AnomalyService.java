package com.alwaysrighttempinc.restapiservice.service;

import com.alwaysrighttempinc.restapiservice.dto.RoomDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerWithAnomalyCountDTO;

import java.util.List;

public interface AnomalyService {
    ThermometerDTO getAnomaliesByThermometerId(String thermometerId);
    RoomDTO getAnomaliesPerRoomId(String roomId);
    List<ThermometerWithAnomalyCountDTO> getThermometersWithAnomaliesCountAboveThreshold();
}
