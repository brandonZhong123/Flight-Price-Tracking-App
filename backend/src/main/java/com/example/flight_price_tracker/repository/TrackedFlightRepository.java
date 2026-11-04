package com.example.flight_price_tracker.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.flight_price_tracker.model.TrackedFlight;

public interface TrackedFlightRepository extends CrudRepository<TrackedFlight, Long> {

} 
