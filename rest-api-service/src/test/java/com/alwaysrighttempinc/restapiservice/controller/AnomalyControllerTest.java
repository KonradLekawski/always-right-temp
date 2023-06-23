package com.alwaysrighttempinc.restapiservice.controller;

import com.alwaysrighttempinc.restapiservice.dto.AnomalyDTO;
import com.alwaysrighttempinc.restapiservice.dto.RoomDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerDTO;
import com.alwaysrighttempinc.restapiservice.dto.ThermometerWithAnomalyCountDTO;
import com.alwaysrighttempinc.restapiservice.service.AnomalyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
class AnomalyControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected AnomalyService anomalyService;

    @BeforeEach
    public void setUp() {
        Mockito.reset(anomalyService);
    }

    @Test
    void shouldReturnThermometersWithAnomaliesCount() throws Exception {
        // given
        List<ThermometerWithAnomalyCountDTO> thermometersWithAnomalyCount = new ArrayList<>();
        thermometersWithAnomalyCount.add(new ThermometerWithAnomalyCountDTO("ThermalPro", 3));
        thermometersWithAnomalyCount.add(new ThermometerWithAnomalyCountDTO("ThermalPro-2", 1));

        // when
        when(anomalyService.getThermometersWithAnomaliesCountAboveThreshold())
                .thenReturn(thermometersWithAnomalyCount);

        // then
        mockMvc.perform(get("/v1/anomalies/thermometers").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].thermometerId", is("ThermalPro")))
                .andExpect(jsonPath("$[0].anomaliesCount", is(3)))
                .andExpect(jsonPath("$[1].thermometerId", is("ThermalPro-2")))
                .andExpect(jsonPath("$[1].anomaliesCount", is(1)));
    }

    @Test
    void shouldReturnAnomaliesForRoom() throws Exception {
        // given
        List<AnomalyDTO> anomalies = new ArrayList<>();
        anomalies.add(new AnomalyDTO("anomaly1", "thermometer1", "1",
                10000L, 25.0D, 5.0D));
        RoomDTO roomDTO = new RoomDTO("1", anomalies);

        // when
        when(anomalyService.getAnomaliesPerRoomId("1"))
                .thenReturn(roomDTO);

        // then
        mockMvc.perform(get("/v1/anomalies/rooms/1").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId", is("1")))
                .andExpect(jsonPath("$.anomalies", hasSize(1)))
                .andExpect(jsonPath("$.anomalies[0].anomalyId", is("anomaly1")))
                .andExpect(jsonPath("$.anomalies[0].roomId", is("1")))
                .andExpect(jsonPath("$.anomalies[0].thermometerId", is("thermometer1")))
                .andExpect(jsonPath("$.anomalies[0].timestamp", is(10000)))
                .andExpect(jsonPath("$.anomalies[0].temperature", is(25.0D)))
                .andExpect(jsonPath("$.anomalies[0].temperatureDifference", is(5.0D)));
    }

    @Test
    void shouldReturnAnomaliesForThermometer() throws Exception {
        // given
        List<AnomalyDTO> anomalies = new ArrayList<>();
        anomalies.add(new AnomalyDTO("anomaly1", "1", "room1",
                10000L, 25.0D, 5.0D));
        ThermometerDTO thermometerDTO = new ThermometerDTO("1", anomalies);

        // when
        when(anomalyService.getAnomaliesByThermometerId("1"))
                .thenReturn(thermometerDTO);

        // then
        mockMvc.perform(get("/v1/anomalies/thermometers/1").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.thermometerId", is("1")))
                .andExpect(jsonPath("$.anomalies", hasSize(1)))
                .andExpect(jsonPath("$.anomalies[0].anomalyId", is("anomaly1")))
                .andExpect(jsonPath("$.anomalies[0].roomId", is("room1")))
                .andExpect(jsonPath("$.anomalies[0].thermometerId", is("1")))
                .andExpect(jsonPath("$.anomalies[0].timestamp", is(10000)))
                .andExpect(jsonPath("$.anomalies[0].temperature", is(25.0D)))
                .andExpect(jsonPath("$.anomalies[0].temperatureDifference", is(5.0D)));
    }
}