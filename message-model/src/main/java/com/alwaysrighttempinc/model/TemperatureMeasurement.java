package com.alwaysrighttempinc.model;

public record TemperatureMeasurement(String measurementId,
                                     String thermometerId,
                                     String roomId,
                                     long timestamp,
                                     double temperature) {
}
