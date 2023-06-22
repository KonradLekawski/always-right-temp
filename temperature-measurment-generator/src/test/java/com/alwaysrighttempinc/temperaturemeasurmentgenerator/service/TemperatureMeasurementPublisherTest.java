package com.alwaysrighttempinc.temperaturemeasurmentgenerator.service;

import com.alwaysrighttempinc.temperaturemeasurmentgenerator.model.TemperatureMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.springframework.kafka.core.KafkaTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

class TemperatureMeasurementPublisherTest {

    @Mock
    private KafkaTemplate<String, TemperatureMeasurement> kafkaTemplate;

    @Captor
    private ArgumentCaptor<String> topicArgumentCaptor;

    @Captor
    private ArgumentCaptor<TemperatureMeasurement> temperatureMeasurementArgumentCaptor;

    private TemperatureMeasurementPublisher publisher;

    @BeforeEach
    public void setUp() {
        openMocks(this);
        String topic = "test-topic";
        publisher = new TemperatureMeasurementPublisher(topic, kafkaTemplate);
    }

    @Test
    void testPublish() {
        //given
        TemperatureMeasurement measurement = new TemperatureMeasurement( "id","device1",
                "location1", System.currentTimeMillis(), 20.0);

        //when
        publisher.publish(measurement);

        //then
        verify(kafkaTemplate, times(1)).send(topicArgumentCaptor.capture(), temperatureMeasurementArgumentCaptor.capture());
        assertThat(topicArgumentCaptor.getValue()).isEqualTo("test-topic");
        assertThat(temperatureMeasurementArgumentCaptor.getValue()).isEqualTo(measurement);
    }
}