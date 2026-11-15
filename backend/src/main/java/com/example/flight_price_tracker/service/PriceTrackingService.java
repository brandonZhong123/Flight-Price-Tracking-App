package com.example.flight_price_tracker.service;

import com.example.flight_price_tracker.client.FlightPriceClient;
import com.example.flight_price_tracker.model.PriceSnapshot;
import com.example.flight_price_tracker.model.TrackedFlight;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.repository.PriceSnapshotRepository;
import com.example.flight_price_tracker.repository.TrackedFlightRepository;
import jakarta.mail.MessagingException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;




@Service
@Slf4j
public class PriceTrackingService {

    private final FlightPriceClient flightPriceClient;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final EmailService emailService;
    private final TrackedFlightRepository trackedFlightRepository;


    public PriceTrackingService(
            FlightPriceClient flightPriceClient,
            PriceSnapshotRepository priceSnapshotRepository,
            EmailService emailService,
            TrackedFlightRepository trackedFlightRepository
    ) {
        this.flightPriceClient = flightPriceClient;
        this.priceSnapshotRepository = priceSnapshotRepository;
        this.emailService = emailService;
        this.trackedFlightRepository = trackedFlightRepository;
    }


    @Transactional
    public Optional<PriceSnapshot> savePriceSnapshot(Long flightId) {
        Optional<BigDecimal> price;
        TrackedFlight flight = trackedFlightRepository.findById(flightId).orElseThrow();

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

            if (flight.getTargetPrice() != null && value.compareTo(flight.getTargetPrice()) <= 0) {
                sendPriceUpdateEmail(flight.getUser(), flight, value);
            }
            return priceSnapshotRepository.save(snapshot);

        });
    }

    @Transactional
    public List<PriceSnapshot> getAllUserPriceSnapshots(TrackedFlight trackedFlight) {
        return priceSnapshotRepository.findByTrackedFlightIdOrderByCheckedAtDesc(trackedFlight.getId());
    }

    @Transactional
    public void sendPriceUpdateEmail(User user, TrackedFlight flight, BigDecimal price) {
        String subject = "Ticket price below target value";
        String html = """
        <html>
        <body style="font-family: Arial, sans-serif; color: #333;">
            <h2>Your flight hit its target price!</h2>
            <p>A flight you're tracking is now at or below your target price.</p>

            <table cellpadding="8" style="border-collapse: collapse;">
                <tr><td><strong>Route</strong></td><td>%s → %s</td></tr>
                <tr><td><strong>Departure</strong></td><td>%s</td></tr>
                <tr><td><strong>Return</strong></td><td>%s</td></tr>
                <tr><td><strong>Current price</strong></td><td>%s %s</td></tr>
                <tr><td><strong>Target price</strong></td><td>%s %s</td></tr>
            </table>

            <p>Prices can change, so check availability before booking.</p>
            <p>Flight Price Tracker</p>
        </body>
        </html>
        """.formatted(
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate(),
                flight.getReturnDate() != null
                        ? flight.getReturnDate().toString()
                        : "One way",
                flight.getCurrency(),
                price.toPlainString(),
                flight.getCurrency(),
                flight.getTargetPrice().toPlainString()
        );

        try {
            emailService.sendEmail(user.getEmail(), subject, html);
        } catch (MessagingException | MailException e) {
            // Don't fail the request: the user can call /auth/resend once mail is working.
            log.error("Failed to price update email to {}", user.getEmail(), e);
        }
    }


}
