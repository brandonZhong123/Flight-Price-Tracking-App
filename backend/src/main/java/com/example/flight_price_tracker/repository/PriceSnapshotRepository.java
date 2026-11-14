package com.example.flight_price_tracker.repository;

import com.example.flight_price_tracker.model.PriceSnapshot;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceSnapshotRepository extends CrudRepository<PriceSnapshot, Long> {

    List<PriceSnapshot> findByTrackedFlightIdOrderByCheckedAtDesc(Long FlightId);
}
