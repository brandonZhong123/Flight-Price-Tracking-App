package com.example.flight_price_tracker.service;

import com.example.flight_price_tracker.dto.RegisterUserDto;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public User signup(RegisterUserDto dto) {
        User user = new User(dto.username(), dto.email(), passwordEncoder.encode(dto.password()));
        user.setVerificationCode(generateVerificationCode());
        user.setEnabled(false);
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    private String generateVerificationCode() {
        Random rand = new Random();
        int code = rand.nextInt(90000) + 100000;
        return String.valueOf(code);
    }

}
