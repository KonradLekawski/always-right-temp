package com.alwaysrighttempinc.anomalydetectionservice.service;

import com.alwaysrighttempinc.anomalydetectionservice.repository.AnomalyRepository;
import com.alwaysrighttempinc.model.Anomaly;
import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AnomalyDetectionServiceTWOTest {

    private AnomalyRepository anomalyRepositoryMock;

    private AnomalyDetectionServiceTWO detectionService;

    @BeforeEach
    public void setup() {
        anomalyRepositoryMock = Mockito.mock(AnomalyRepository.class);
        detectionService = new AnomalyDetectionServiceTWO(5.0, anomalyRepositoryMock);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenAnomalyRepositoryIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new AnomalyDetectionServiceTWO(5.0, null));
    }

    @Test
    void shouldDetectAnomalyInTenSecondsWindowWhenTemperatureIsAboveAveragePlusThreshold() {
        //given
        long baseTimestamp = 1000000L;
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", baseTimestamp, 10.0);
        TemperatureMeasurement anomaly = new TemperatureMeasurement("id", "thermometerId",
                "roomId", baseTimestamp + 9, 20.0);
        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(10, measurement));
        measurements.add(anomaly);

        //when
        detectionService.process(measurements);

        //then
        verify(anomalyRepositoryMock, times(1)).save(any(Anomaly.class));
    }

    @Test
    void shouldNotDetectAnomalyWhenMeasurementOutsideTheWindow() {
        //given
        long baseTimestamp = 1000000L;
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", baseTimestamp, 10.0);
        TemperatureMeasurement anomaly = new TemperatureMeasurement("id", "thermometerId",
                "roomId", baseTimestamp + 15, 20.0);
        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(10, measurement));
        measurements.add(anomaly);

        //when
        detectionService.process(measurements);

        //then
        verify(anomalyRepositoryMock, times(0)).save(any(Anomaly.class));
    }

    @Test
    void shouldNotDetectAnomalyWhenMeasurementInWindowButTemperatureBelowAveragePlusThreshold() {
        //given
        long baseTimestamp = 1000000L;
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", baseTimestamp, 10.0);
        TemperatureMeasurement notAnomaly = new TemperatureMeasurement("id", "thermometerId",
                "roomId", baseTimestamp + 9, 14.0);
        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(10, measurement));
        measurements.add(notAnomaly);

        //when
        detectionService.process(measurements);

        //then
        verify(anomalyRepositoryMock, times(0)).save(any(Anomaly.class));
    }

    @Test
    void shouldNotDetectAnomalyWhenMeasurementsCollectionIsEmpty() {
        detectionService.process(Collections.emptyList());

        verify(anomalyRepositoryMock, times(0)).save(any(Anomaly.class));
    }

    @Test
    void shouldThrowNullPointerExceptionWhenMeasurementCollectionIsNull() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> detectionService.process(null));
    }
}