package com.example.flight_price_tracker.service;

import com.example.flight_price_tracker.client.FlightPriceClient;
import com.example.flight_price_tracker.model.PriceSnapshot;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.repository.PriceSnapshotRepository;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClientException;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;




@Service
public class PriceTrackingService {

    private final FlightPriceClient flightPriceClient;
    private final PriceSnapshotRepository priceSnapshotRepository;


    public PriceTrackingService(FlightPriceClient flightPriceClient, PriceSnapshotRepository priceSnapshotRepository) {
        this.flightPriceClient = flightPriceClient;
        this.priceSnapshotRepository = priceSnapshotRepository;
    }


    public Optional<PriceSnapshot> savePriceSnapshot(TrackedFlight flight) {
        Optional<BigDecimal> price;

        try {
            price = flightPriceClient.getLowestPrice(flight);
        } catch (RestClientException | IllegalStateException e) {
            log.warn("Price fetch failed for tracked flight {} ({})",
                    flight.getId(), e.getClass().getSimpleName());

            return Optional.empty();
        }

        return price.map(value -> {
            PriceSnapshot snapshot = new PriceSnapshot(
                    flight,
                    Instant.now(),
                    value
            );

            return priceSnapshotRepository.save(snapshot);
        });
    }
}
