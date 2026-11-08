package com.example.flight_price_tracker.controller;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.dto.trackedFlight.UpdateTrackedFlightDto;
import com.example.flight_price_tracker.mapper.TrackedFlightMapper;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.responses.TrackedFlightResponse;
import com.example.flight_price_tracker.service.TrackedFlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tracked-flights")
public class TrackedFlightController {

    private final TrackedFlightService trackedFlightService;

    public TrackedFlightController(TrackedFlightService trackedFlightService) {
        this.trackedFlightService = trackedFlightService;
    }

    @PostMapping("/create")
    public ResponseEntity<TrackedFlightResponse> createTrackedFlight(
            @Valid @RequestBody TrackedFlightDto dto,
            @AuthenticationPrincipal User user
    ) {
        TrackedFlight trackedFlight = trackedFlightService.createTrackedFlight(dto, user);
        return ResponseEntity.ok(TrackedFlightMapper.trackedFlightToRes(trackedFlight));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackedFlightResponse> getTrackedFlight(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        TrackedFlight trackedFlight = trackedFlightService.getTrackedFlight(user, id);
        return ResponseEntity.ok(TrackedFlightMapper.trackedFlightToRes(trackedFlight));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TrackedFlightResponse> updateTrackedFlight(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody UpdateTrackedFlightDto dto
    ) {
        TrackedFlight trackedFlight = trackedFlightService.updateTrackedFlight(user, id, dto);
        return ResponseEntity.ok(TrackedFlightMapper.trackedFlightToRes(trackedFlight));
    }

    // 204 No Content: the delete succeeded and there's nothing to return
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrackedFlight(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        trackedFlightService.deleteTrackedFlight(user, id);
        return ResponseEntity.noContent().build();
    }
}
