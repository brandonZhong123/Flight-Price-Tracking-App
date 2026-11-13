package com.example.flight_price_tracker.client;

import com.example.flight_price_tracker.dto.api.SerpApiResponse;
import com.example.flight_price_tracker.model.TrackedFlight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Stream;

@Component
public class FlightPriceClient {

    @Value("${security.serp.api.key}")
    private String apiKey;

    private final RestClient restClient;

    public FlightPriceClient(RestClient.Builder builder) {
        this.restClient = builder.
                baseUrl("https://serpapi.com").
                build();
    }

    public Optional<BigDecimal> getLowestPrice(TrackedFlight flight) {
        boolean roundTrip = flight.getReturnDate() != null;
        SerpApiResponse response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/search")
                        .queryParam("api_key", apiKey)
                        .queryParam("departure_id", flight.getOrigin())
                        .queryParam("arrival_id", flight.getDestination())
                        .queryParam("outbound_date", flight.getDepartureDate())
                        .queryParam("currency", flight.getCurrency())
                        .queryParam("type", roundTrip ? 1:2)
                        .queryParam("adults", 1).queryParam("engine", "google_flights");

                    if (roundTrip) {
                        uriBuilder.queryParam("return_date", flight.getReturnDate());
                    }

                    return uriBuilder.build();
                })
                .retrieve()
                .body(SerpApiResponse.class);
        if (response == null) {
            throw new IllegalStateException("SerpApi returned an empty response");
        }

        if (response.error() != null) {
            throw new IllegalStateException("SerpApi search failed: " + response.error());
        }

        return Stream.of(response.bestFlights(), response.otherFlights())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(SerpApiResponse.FlightOption::price)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo);
    }

}
