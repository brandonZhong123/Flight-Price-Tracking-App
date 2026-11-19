package com.example.flight_price_tracker.scheduler;

import com.example.flight_price_tracker.model.TrackedFlight;
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

    // initialDelay keeps the job from firing on startup, which would spend API
    // quota on every restart. First run is one day after boot, then daily.
    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void checkPrices() {
        for (TrackedFlight trackedFlight : trackedFlightRepository.findByDepartureDateGreaterThanEqual(LocalDate.now())) {
            try {
                priceTrackingService.savePriceSnapshot(trackedFlight.getId());
            } catch (RuntimeException e) {
                log.warn("Price check failed for tracked flight {} ({})",
                        trackedFlight.getId(), e.getClass().getSimpleName());
            }
        }
    }
}
