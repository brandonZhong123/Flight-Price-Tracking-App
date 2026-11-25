package com.example.flight_price_tracker.controller;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.dto.trackedFlight.UpdateTrackedFlightDto;
import com.example.flight_price_tracker.mapper.TrackedFlightMapper;
import com.example.flight_price_tracker.model.PriceSnapshot;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.responses.PriceSnapshotResponse;
import com.example.flight_price_tracker.responses.TrackedFlightResponse;
import com.example.flight_price_tracker.service.PriceTrackingService;
import com.example.flight_price_tracker.service.TrackedFlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/tracked-flights")
public class TrackedFlightController {

    private final TrackedFlightService trackedFlightService;
    private final PriceTrackingService priceTrackingService;

    public TrackedFlightController(
            TrackedFlightService trackedFlightService,
            PriceTrackingService priceTrackingService
    ) {
        this.trackedFlightService = trackedFlightService;
        this.priceTrackingService = priceTrackingService;
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

    @GetMapping("/my-tracked-flights")
    public ResponseEntity<List<TrackedFlightResponse>> getUserTrackedFlights(
            @AuthenticationPrincipal User user
    ) {
        List<TrackedFlight> trackedFlights = trackedFlightService.getUserTrackedFlights(user);
        List<TrackedFlightResponse> trackedFlightResponses = trackedFlights.stream()
                .map(TrackedFlightMapper::trackedFlightToRes)
                .toList();
        return ResponseEntity.ok(trackedFlightResponses);
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

    @GetMapping("/{id}/snapshots")
    public ResponseEntity<List<PriceSnapshotResponse>> getUserFlightPriceSnapshot(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        TrackedFlight trackedFlight = trackedFlightService.getTrackedFlight(user, id);
        List<PriceSnapshot> priceSnapshots = priceTrackingService.getAllUserPriceSnapshots(trackedFlight);
        List<PriceSnapshotResponse> priceSnapshotResponses = priceSnapshots.stream()
                .map(TrackedFlightMapper::priceSnapshotToRes)
                .toList();
        return ResponseEntity.ok(priceSnapshotResponses);
    }

    // Trigger a price fetch on demand instead of waiting for the daily scheduler.
    // Useful for testing the price path. getTrackedFlight enforces ownership
    // (404 if the flight isn't the caller's). Returns the new snapshot, or 204
    // if the fetch found no price.
    @PostMapping("/{id}/check")
    public ResponseEntity<PriceSnapshotResponse> checkPriceNow(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        trackedFlightService.getTrackedFlight(user, id);
        return priceTrackingService.savePriceSnapshot(id)
                .map(TrackedFlightMapper::priceSnapshotToRes)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }


}
