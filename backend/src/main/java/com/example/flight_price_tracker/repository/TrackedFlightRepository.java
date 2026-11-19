package com.example.flight_price_tracker.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.flight_price_tracker.model.TrackedFlight;

public interface TrackedFlightRepository extends CrudRepository<TrackedFlight, Long> {

    // Matches only if the flight belongs to the given user (WHERE id = ? AND user_id = ?)
    Optional<TrackedFlight> findByIdAndUserId(Long id, Long userId);
    List<TrackedFlight> findByUserIdOrderByCreatedAtAsc(Long userId);

    // Flights still worth checking: departure today or later. Filters in the
    // database so the scheduler doesn't load past flights it would skip anyway.
    List<TrackedFlight> findByDepartureDateGreaterThanEqual(LocalDate date);
}
