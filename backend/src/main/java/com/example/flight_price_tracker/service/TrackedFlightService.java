package com.example.flight_price_tracker.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.dto.trackedFlight.UpdateTrackedFlightDto;
import com.example.flight_price_tracker.mapper.TrackedFlightMapper;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.repository.TrackedFlightRepository;

import java.util.List;

@Service
public class TrackedFlightService {

    private final TrackedFlightRepository trackedFlightRepository;

    public TrackedFlightService(TrackedFlightRepository trackedFlightRepository) {
        this.trackedFlightRepository = trackedFlightRepository;
    }

    // Create new Tracked Flight
    public TrackedFlight createTrackedFlight(TrackedFlightDto dto, User user) {
        if (dto.returnDate() != null && dto.returnDate().isBefore(dto.departureDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Return date must be on or after departure date");
        }

        return trackedFlightRepository.save(
                TrackedFlightMapper.trackedFlightDtoToTrackedFlight(dto, user));
    }

    // Get tracked flight
    @Transactional(readOnly = true)
    public TrackedFlight getTrackedFlight(User user, Long id) {
        return findOwnedOrThrow(user, id);
    }

    // Get all of a users tracked flight

    @Transactional(readOnly = true)
    public List<TrackedFlight> getUserTrackedFlights(User user) {
        return trackedFlightRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    // Update the target price. No save() needed: Hibernate writes the change on commit.
    @Transactional
    public TrackedFlight updateTrackedFlight(User user, Long id, UpdateTrackedFlightDto dto) {
        TrackedFlight trackedFlight = findOwnedOrThrow(user, id);
        trackedFlight.setTargetPrice(dto.targetPrice());
        return trackedFlight;
    }

    // Delete tracked flight (its price snapshots are removed by the cascade)
    @Transactional
    public void deleteTrackedFlight(User user, Long id) {
        trackedFlightRepository.delete(findOwnedOrThrow(user, id));
    }

    // Returns 404 both when the flight doesn't exist and when it belongs to someone else,
    // so other users can't discover which IDs exist.
    private TrackedFlight findOwnedOrThrow(User user, Long id) {
        return trackedFlightRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tracked flight not found"));
    }
}
