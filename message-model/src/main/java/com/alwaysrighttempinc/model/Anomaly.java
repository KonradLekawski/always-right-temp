package com.alwaysrighttempinc.model;

public record Anomaly(String anomalyId,
                      String measurementId,
                      String thermometerId,
                      String roomId,
                      long timestamp,
                      double temperature) {
}
