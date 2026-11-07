package com.example.flight_price_tracker.controller;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.mapper.TrackedFlightMapper;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.responses.TrackedFlightResponse;
import com.example.flight_price_tracker.service.TrackedFlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/flights")
public class TrackedFlightController {

    private TrackedFlightService trackedFlightService;


    public TrackedFlightController(TrackedFlightService trackedFlightService) {
        this.trackedFlightService = trackedFlightService;
    }

    public ResponseEntity<TrackedFlightResponse> createTrackedFlight(
            TrackedFlightDto dto,
            @AuthenticationPrincipal User user
    ) {
        TrackedFlight trackedFlight = trackedFlightService.createTrackedFlight(dto, user);
        TrackedFlightResponse res = TrackedFlightMapper.trackedFlightToRes(trackedFlight);
        return ResponseEntity.ok(res);
    }

}
