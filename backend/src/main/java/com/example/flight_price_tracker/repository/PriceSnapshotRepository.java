package com.example.flight_price_tracker.repository;

import com.example.flight_price_tracker.model.PriceSnapshot;
import org.springframework.data.repository.CrudRepository;

public interface PriceSnapshotRepository extends CrudRepository<PriceSnapshot, Long> {
}
