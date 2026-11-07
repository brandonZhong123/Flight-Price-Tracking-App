package com.example.flight_price_tracker.service;

import java.time.LocalDate;
import java.util.Optional;

import com.example.flight_price_tracker.dto.trackedFlight.UpdateTrackedFlightDto;
import com.example.flight_price_tracker.mapper.TrackedFlightMapper;
import com.example.flight_price_tracker.responses.TrackedFlightResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.repository.TrackedFlightRepository;
import com.example.flight_price_tracker.repository.UserRepository;

@Service 
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
               
        return trackedFlightRepository.save(
                TrackedFlightMapper
                        .trackedFlightDtoToTrackedFlight(dto, user));
        
    }

    // Get tracked flight

    public TrackedFlight getTrackedFlight(User user, Long id) {
        TrackedFlight trackedFlight = trackedFlightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracked flight not found"));

        if (!trackedFlight.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not own this tracked flight");
        }

        return trackedFlight;
    }

    // Update Tracked Flight
    public TrackedFlight updateTrackedFlight(User user, Long id, UpdateTrackedFlightDto dto) {
        TrackedFlight trackedFlight = trackedFlightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracked flight not found"));
        if (!trackedFlight.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not own this tracked flight");
        }

        return trackedFlight;
    }

    public void deleteTrackedFlight(User user, Long id) {
        TrackedFlight trackedFlight = trackedFlightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracked flight not found"));
        if (!trackedFlight.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not own this tracked flight");
        }

        trackedFlightRepository.deleteById(id);
    }

}
