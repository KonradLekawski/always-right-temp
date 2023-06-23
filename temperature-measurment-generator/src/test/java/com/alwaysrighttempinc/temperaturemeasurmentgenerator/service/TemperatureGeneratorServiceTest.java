package com.alwaysrighttempinc.temperaturemeasurmentgenerator.service;

import com.alwaysrighttempinc.model.TemperatureMeasurement;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.util.Random;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class TemperatureGeneratorServiceTest {
    @Mock
    private Random random;

    @Mock
    private TemperatureMeasurementPublisher temperatureMeasurementPublisher;

    @Captor
    private ArgumentCaptor<TemperatureMeasurement> captor;

    private TemperatureGeneratorService service;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        double baseTemperature = 20.0;
        long batchSize = 1L;
        double temperatureThreshold = 5.0;
        double anomalyLikelihood = 0.5;
        service = new TemperatureGeneratorService(baseTemperature, batchSize, temperatureThreshold, anomalyLikelihood, random, temperatureMeasurementPublisher);
    }

    @Test
    void shouldGenerateAndSendMeasurement() {
        double normalTemperatureIncrease = 0.1;
        double nonAnomalyValue = 0.6;

        when(random.nextDouble()).thenReturn(normalTemperatureIncrease, nonAnomalyValue);

        service.generateAndSendTemperatureData();

        verify(temperatureMeasurementPublisher, times(1)).publish(captor.capture());
        Assertions.assertThat(captor.getValue().temperature()).isBetween(20.0, 21.0);
    }

    @Test
    void shouldGenerateAndSendAnomaly() {
        //given
        double anomalyTemperatureIncrease = 0.1;
        double anomalyValue = 0.4;
        double additionalAnomalyIncrease = 0.1;
        when(random.nextDouble()).thenReturn(anomalyTemperatureIncrease, anomalyValue, additionalAnomalyIncrease);

        //when
        service.generateAndSendTemperatureData();

        //then
        verify(temperatureMeasurementPublisher, times(1)).publish(captor.capture());
        Assertions.assertThat(captor.getValue().temperature()).isGreaterThanOrEqualTo(25.0);
    }
}
