package com.alwaysrighttempinc.anomalydetectionservice.repository;

import com.alwaysrighttempinc.model.Anomaly;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnomalyRepository extends MongoRepository<Anomaly, String> {
}
