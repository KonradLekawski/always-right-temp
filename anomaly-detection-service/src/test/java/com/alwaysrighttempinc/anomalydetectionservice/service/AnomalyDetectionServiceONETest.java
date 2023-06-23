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

class AnomalyDetectionServiceONETest {

    private AnomalyRepository anomalyRepositoryMock;

    private AnomalyDetectionServiceONE detectionService;

    @BeforeEach
    public void setup() {
        anomalyRepositoryMock = Mockito.mock(AnomalyRepository.class);
        detectionService = new AnomalyDetectionServiceONE(1.0, anomalyRepositoryMock);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenAnomalyRepositoryIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new AnomalyDetectionServiceONE(1.0, null));
    }

    @Test
    void shouldDetectOneAnomalyWhenTemperatureIsAboveThreshold() {
        //given measurements with one anomaly
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 5.0);
        TemperatureMeasurement anomaly = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 10.0);
        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(10, measurement));
        measurements.add(anomaly);

        //when
        detectionService.process(measurements);

        //then
        verify(anomalyRepositoryMock, times(1)).save(any(Anomaly.class));
    }

    @Test
    void shouldDetectTwoAnomaliesWhenTemperatureIsAboveThreshold() {
        //given measurements with two anomalies
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 5.0);
        TemperatureMeasurement anomalyOne = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 10.0);
        TemperatureMeasurement anomalyTwo = new TemperatureMeasurement("id", "thermometerId1",
                "roomId1", System.currentTimeMillis(), 7.0);
        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(10, measurement));
        measurements.add(anomalyOne);
        measurements.add(anomalyTwo);

        //when
        detectionService.process(measurements);

        //then
        verify(anomalyRepositoryMock, times(2)).save(any(Anomaly.class));
    }

    @Test
    void shouldNotDetectAnomalyWhenTemperatureIsBelowThreshold() {
        //given measurements with two anomalies
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 10.0);
        TemperatureMeasurement notAnomaly = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 10.7);

        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(10, measurement));
        measurements.add(notAnomaly);

        //when
        detectionService.process(measurements);

        //then
        verify(anomalyRepositoryMock, times(0)).save(any(Anomaly.class));
    }

    @Test
    void shouldNotDetectAnomaliesWhenLessThanTenMeasurements() {
        //given measurements with two anomalies
        TemperatureMeasurement measurement = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 10.0);
        TemperatureMeasurement anomaly = new TemperatureMeasurement("id", "thermometerId",
                "roomId", System.currentTimeMillis(), 10.0);
        Collection<TemperatureMeasurement> measurements = new ArrayList<>(Collections.nCopies(5, measurement));
        measurements.add(anomaly);

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