package com.example.flight_price_tracker.client;

import com.example.flight_price_tracker.model.TrackedFlight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.OptionalInt;

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
        String response = restClient.get()
                .uri(uriBuilder -> { uriBuilder.path("/search")
                        .queryParam("api_key", apiKey)
                        .queryParam("departure_id", flight.getOrigin())
                        .queryParam("arrival_id", flight.getDestination())
                        .queryParam("outbound_date", flight.getDepartureDate())
                        .queryParam("currency", flight.getCure())
                        .queryParam("type", roundTrip ? 1:2)
                        .queryParam("adults", 1);

                    if (roundTrip) {
                        uriBuilder.queryParam("return_date", flight.getReturnDate());
                    }

                    return uriBuilder.build();
                })
    }

}
