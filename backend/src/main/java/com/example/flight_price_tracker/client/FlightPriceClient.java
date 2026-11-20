package com.example.flight_price_tracker.client;

import com.example.flight_price_tracker.dto.api.SerpApiResponse;
import com.example.flight_price_tracker.model.TrackedFlight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class FlightPriceClient {

    // Fail fast if serpapi.com can't be reached; connecting is quick when it works.
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    // A SerpApi flight search scrapes Google Flights, so give the response room.
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(20);

    @Value("${security.serp.api.key}")
    private String apiKey;

    // Built directly rather than injecting RestClient.Builder, which is only
    // auto-configured when spring-boot-starter-web is on the classpath.
    // Timeouts turn a hung request into a quick failure, so one stuck flight
    // can't block the scheduler loop or hold a DB transaction open.
    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://serpapi.com")
            .requestFactory(requestFactory())
            .build();

    private static SimpleClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return factory;
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
