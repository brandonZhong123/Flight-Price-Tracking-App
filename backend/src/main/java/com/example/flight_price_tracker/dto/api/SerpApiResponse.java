package com.example.flight_price_tracker.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SerpApiResponse(
        @JsonProperty("best_flights") List<FlightOption> bestFlights,
        @JsonProperty("other_flights")List<FlightOption> otherFlights,
        String error
        ) {

    @JsonIgnoreProperties(ignoreUnknown = true)
            public record FlightOption(BigDecimal price) {}
}
