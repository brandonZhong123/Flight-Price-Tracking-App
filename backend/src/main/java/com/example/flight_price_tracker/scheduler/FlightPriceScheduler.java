package com.example.flight_price_tracker.scheduler;

import com.example.flight_price_tracker.model.PriceSnapshot;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.repository.PriceSnapshotRepository;
import com.example.flight_price_tracker.repository.TrackedFlightRepository;
import com.example.flight_price_tracker.service.PriceTrackingService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FlightPriceScheduler {

    private final TrackedFlightRepository trackedFlightRepository;
    private final PriceTrackingService priceTrackingService;

    public FlightPriceScheduler(
            TrackedFlightRepository trackedFlightRepository,
            PriceTrackingService priceTrackingService
    ) {
        this.trackedFlightRepository = trackedFlightRepository;
        this.priceTrackingService = priceTrackingService;
    }

    @Scheduled(fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    public void checkPrices() {
        LocalDate today = LocalDate.now();

        for (TrackedFlight trackedFlight: trackedFlightRepository.findAll()) {
            if (trackedFlight.getDepartureDate().isBefore(today)) {
                continue;
            }

            try {
                priceTrackingService.savePriceSnapshot(trackedFlight);
            } catch(RuntimeException e) {
                log.warn("Price check failed for tracked flight {} ({})",
                        trackedFlight.getId(), e.getClass().getSimpleName());
            }
        }
    }
}
