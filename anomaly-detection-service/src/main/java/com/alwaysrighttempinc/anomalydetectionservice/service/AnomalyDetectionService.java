package com.alwaysrighttempinc.anomalydetectionservice.service;

import java.util.Collection;

public interface AnomalyDetectionService<T> {
    void process(Collection<T> measurements);
}
