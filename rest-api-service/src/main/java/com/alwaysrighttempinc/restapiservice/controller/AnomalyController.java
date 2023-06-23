package com.alwaysrighttempinc.restapiservice.controller;

import com.alwaysrighttempinc.restapiservice.dto.RoomDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerWithAnomalyCountDTO;
import com.alwaysrighttempinc.restapiservice.service.AnomalyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/anomalies")
@Tag(name = "User", description = "User APIs")
public class AnomalyController {
    private final AnomalyService anomalyService;

    public AnomalyController(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @GetMapping("/thermometers")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Thermometers above anomaly count threshold",
            description = "Returns all thermometers that contain more than preconfigured amount of anomalies")
    public  List<ThermometerWithAnomalyCountDTO> getAnomaliesByRoomId() {
        return anomalyService.getThermometersWithAnomaliesCountAboveThreshold();
    }

    @GetMapping("/thermometers/{thermometerId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Anomalies by thermometerId", description = "Returns anomalies per given thermometerID")
    public ThermometerDTO getAnomaliesByThermometer(@PathVariable(name = "thermometerId") String thermometerId) {
        return anomalyService.getAnomaliesByThermometerId(thermometerId);
    }

    @GetMapping("/rooms/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Anomalies by roomId", description = "Returns anomalies per given roomId")
    public RoomDTO getAnomaliesByRoomId(@PathVariable(name = "roomId") String roomId) {
        return anomalyService.getAnomaliesPerRoomId(roomId);
    }
}
