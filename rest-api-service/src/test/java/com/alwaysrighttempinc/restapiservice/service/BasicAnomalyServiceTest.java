package com.alwaysrighttempinc.restapiservice.service;

import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.restapiservice.dto.AnomalyDTO;
import com.alwaysrighttempinc.restapiservice.dto.RoomDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerWithAnomalyCountDTO;
import com.alwaysrighttempinc.restapiservice.repository.AnomalyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class BasicAnomalyServiceTest {

    private static final int ANOMALY_COUNT_THRESHOLD = 2;
    private BasicAnomalyService basicAnomalyService;

    @Mock
    private AnomalyRepository anomalyRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        basicAnomalyService = new BasicAnomalyService(anomalyRepository, ANOMALY_COUNT_THRESHOLD);
    }

    @Test
    void shouldReturnAnomaliesWhenGetAnomaliesByThermometerId() {
        //given
        String thermometerId = "ther1";
        Anomaly anomaly1 = new Anomaly("a1", "M1", thermometerId,"room1", 1633053268000L, 30.0, 5.0);
        Anomaly anomaly2 = new Anomaly("a2", "M2", thermometerId,"room1", 1633053268000L, 30.0, 5.0);
        when(anomalyRepository.findAnomaliesByThermometerId("ther1")).thenReturn(Arrays.asList(anomaly1, anomaly2));

        //when
        ThermometerDTO result = basicAnomalyService.getAnomaliesByThermometerId(thermometerId);

        //then
        assertEquals(thermometerId, result.thermometerId());
        List<AnomalyDTO> anomalies = result.anomalies();
        assertEquals(2, anomalies.size());
        assertEquals("a1", anomalies.get(0).anomalyId());
        assertEquals("a2", anomalies.get(1).anomalyId());
    }

    @Test
    void shouldReturnEmptyWhenNoAnomaliesByThermometerId() {
        //given
        String thermometerId = "1";
        when(anomalyRepository.findAnomaliesByThermometerId("1")).thenReturn(Collections.emptyList());

        //when
        ThermometerDTO result = basicAnomalyService.getAnomaliesByThermometerId(thermometerId);

        //then
        assertEquals(thermometerId, result.thermometerId());
        assertEquals(0, result.anomalies().size());
    }

    @Test
    void shouldReturnAnomaliesWhenGetAnomaliesPerRoomId() {
        //given
        String roomId = "room1";
        Anomaly anomaly1 = new Anomaly("a1", "1", "ther1","room1", 1633053268000L, 30.0, 5.0);
        Anomaly anomaly2 = new Anomaly("a2", "1", "ther1","room1", 1633053268000L, 32.0, 4.0);
        when(anomalyRepository.findAnomaliesByRoomId(roomId)).thenReturn(Arrays.asList(anomaly1, anomaly2));

        //when
        RoomDTO result = basicAnomalyService.getAnomaliesPerRoomId(roomId);

        //then
        assertEquals(roomId, result.roomId());
        List<AnomalyDTO> anomalies = result.anomalies();
        assertEquals(2, anomalies.size());
        assertEquals("a1", anomalies.get(0).anomalyId());
        assertEquals("a2", anomalies.get(1).anomalyId());
    }

    @Test
    void shouldReturnEmptyWhenNoAnomaliesPerRoomId() {
        //given
        String roomId = "1";
        when(anomalyRepository.findAnomaliesByRoomId(roomId)).thenReturn(Collections.emptyList());

        //when
        RoomDTO result = basicAnomalyService.getAnomaliesPerRoomId(roomId);

        //then
        assertEquals(roomId, result.roomId());
        assertEquals(0, result.anomalies().size());
    }

    @Test
    void shouldReturnThermometersWhenAnomaliesCountAboveThreshold() {
        //given
        Anomaly anomaly1 = new Anomaly("a1", "1", "ther1","room1", 1633053268000L, 30.0, 5.0);
        Anomaly anomaly2 = new Anomaly("a2", "1", "ther1","room1", 1633053268000L, 32.0, 4.0);
        Anomaly anomaly3 = new Anomaly("a3", "2", "ther1","room2", 1633053268000L, 33.0, 6.0);
        when(anomalyRepository.findAll()).thenReturn(Arrays.asList(anomaly1, anomaly2, anomaly3));

        //when
        List<ThermometerWithAnomalyCountDTO> result = basicAnomalyService.getThermometersWithAnomaliesCountAboveThreshold();

        //then
        assertEquals(1, result.size());
        assertEquals("ther1", result.get(0).thermometerId());
        assertEquals(3, result.get(0).anomaliesCount());
    }

    @Test
    void shouldReturnEmptyWhenNoThermometersWithAnomaliesCountAboveThreshold() {
        //given
        Anomaly anomaly1 = new Anomaly("a1", "1", "ther1","room1", 1633053268000L, 30.0, 5.0);
        Anomaly anomaly2 = new Anomaly("a2", "2", "ther2","room2", 1633053268000L, 32.0, 4.0);
        Anomaly anomaly3 = new Anomaly("a3", "3", "ther3","room3", 1633053268000L, 33.0, 6.0);
        when(anomalyRepository.findAll()).thenReturn(Arrays.asList(anomaly1, anomaly2, anomaly3));

        //when
        List<ThermometerWithAnomalyCountDTO> result = basicAnomalyService.getThermometersWithAnomaliesCountAboveThreshold();

        //then
        assertEquals(0, result.size());
    }
}
