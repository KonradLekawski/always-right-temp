package com.alwaysrighttempinc.restapiservice.service;

import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.restapiservice.dto.AnomalyDTO;
import com.alwaysrighttempinc.restapiservice.dto.RoomDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerWithAnomalyCountDTO;
import com.alwaysrighttempinc.restapiservice.repository.AnomalyRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class BasicAnomalyService implements AnomalyService {

    private final Integer anomalyCountThreshold;

    private final AnomalyRepository anomalyRepository;

    public BasicAnomalyService(AnomalyRepository anomalyRepository, Integer anomalyCountThreshold) {
        this.anomalyRepository = anomalyRepository;
        this.anomalyCountThreshold = anomalyCountThreshold;
    }

    @Override
    public ThermometerDTO getAnomaliesByThermometerId(String thermometerId) {
        List<AnomalyDTO> anomalies = anomalyRepository.findAnomaliesByThermometerId(thermometerId).stream()
                .map(this::toAnomalyDTO)
                .toList();
        return new ThermometerDTO(thermometerId, anomalies);
    }

    @Override
    public RoomDTO getAnomaliesPerRoomId(String roomId) {
        List<AnomalyDTO> anomalies = anomalyRepository.findAnomaliesByRoomId(roomId).stream()
                .map(this::toAnomalyDTO)
                .toList();
        return new RoomDTO(roomId, anomalies);
    }

    @Override
    public List<ThermometerWithAnomalyCountDTO> getThermometersWithAnomaliesCountAboveThreshold() {
        List<Anomaly> anomalies = anomalyRepository.findAll();
        return anomalies.stream()
                .collect(Collectors.groupingBy(Anomaly::thermometerId))
                .entrySet().stream()
                .filter(entry -> entry.getValue().size() > anomalyCountThreshold)
                .map(this::toThermometerWithAnomaliesCountDto)
                .toList();
    }

    private AnomalyDTO toAnomalyDTO(Anomaly anomaly) {
        return new AnomalyDTO(anomaly.anomalyId(), anomaly.thermometerId(), anomaly.roomId(), anomaly.timestamp(),
                anomaly.temperature(), anomaly.temperatureDifference());
    }

    private ThermometerWithAnomalyCountDTO toThermometerWithAnomaliesCountDto(Map.Entry<String, List<Anomaly>> entry) {
        return new ThermometerWithAnomalyCountDTO(entry.getKey(), entry.getValue().size());
    }
}
