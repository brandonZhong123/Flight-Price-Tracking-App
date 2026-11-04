package com.example.flight_price_tracker.service;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.repository.TrackedFlightRepository;
import com.example.flight_price_tracker.repository.UserRepository;

public class TrackedFlightService {
    

    private TrackedFlightRepository trackedFlightRepository;
    public TrackedFlightService(
        TrackedFlightRepository trackedFlightRepository
    ) {
        this.trackedFlightRepository = trackedFlightRepository;
    } 
    // Create new Tracked Flight
    public TrackedFlight createTrackedFlight(TrackedFlightDto dto, User user) {

        if (dto.returnDate() != null && dto.returnDate().isBefore(dto.departureDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return date must be before departure date");
        }
        
        TrackedFlight flight = new TrackedFlight(
                user,
                dto.origin(),
                dto.destination(),
                dto.departureDate(),
                dto.returnDate(),
                dto.currency(),
                dto.targetPrice());
               
        return trackedFlightRepository.save(flight);
        
    }
}
