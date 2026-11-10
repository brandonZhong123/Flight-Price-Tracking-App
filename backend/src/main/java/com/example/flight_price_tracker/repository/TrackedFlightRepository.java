package com.example.flight_price_tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.flight_price_tracker.model.TrackedFlight;

public interface TrackedFlightRepository extends CrudRepository<TrackedFlight, Long> {

    // Matches only if the flight belongs to the given user (WHERE id = ? AND user_id = ?)
    Optional<TrackedFlight> findByIdAndUserId(Long id, Long userId);
    List<TrackedFlight> findByUserIdOrderByCreatedAtDesc(Long userId);
}
