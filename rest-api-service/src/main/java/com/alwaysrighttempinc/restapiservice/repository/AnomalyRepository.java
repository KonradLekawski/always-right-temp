package com.alwaysrighttempinc.restapiservice.repository;

import com.alwaysrighttempinc.model.Anomaly;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnomalyRepository extends MongoRepository<Anomaly, String> {

    List<Anomaly> findAnomaliesByThermometerId(String thermometerId);
    List<Anomaly> findAnomaliesByRoomId(String roomId);
}
