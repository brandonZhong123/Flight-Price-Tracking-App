package com.example.flight_price_tracker.model;

import jakarta.persistence.Entity;

@Entity
public class User {

    private String username;
    private String email;
    private String password;
    private boolean enabled;
}
