package com.example.flight_price_tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FlightPriceTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightPriceTrackerApplication.class, args);
	}

}
