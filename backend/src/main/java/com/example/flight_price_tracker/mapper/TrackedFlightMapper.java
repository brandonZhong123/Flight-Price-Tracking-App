package com.example.flight_price_tracker.mapper;

import com.example.flight_price_tracker.dto.trackedFlight.TrackedFlightDto;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.responses.TrackedFlightResponse;

public class TrackedFlightMapper {

    public static TrackedFlightResponse trackedFlightToRes(TrackedFlight trackedFlight) {
        return new TrackedFlightResponse(
                trackedFlight.getId(),
                trackedFlight.getOrigin(),
                trackedFlight.getDestination(),
                trackedFlight.getDepartureDate(),
                trackedFlight.getReturnDate(),
                trackedFlight.getCurrency(),
                trackedFlight.getTargetPrice()
                
        );
    }

    public static TrackedFlight trackedFlightDtoToTrackedFlight(TrackedFlightDto dto, User user) {
        return new TrackedFlight(
                user,
                dto.origin(),
                dto.destination(),
                dto.departureDate(),
                dto.returnDate(),
                dto.currency(),
                dto.targetPrice()
        );
    }
}
