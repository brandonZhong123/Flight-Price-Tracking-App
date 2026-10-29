package com.example.flight_price_tracker.controller;

import com.example.flight_price_tracker.dto.LoginUserDto;
import com.example.flight_price_tracker.dto.RegisterUserDto;
import com.example.flight_price_tracker.dto.ResendVerificationDto;
import com.example.flight_price_tracker.dto.VerifyUserDto;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.responses.LoginResponse;
import com.example.flight_price_tracker.responses.UserResponse;
import com.example.flight_price_tracker.service.AuthenticationService;
import com.example.flight_price_tracker.service.JWTService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JWTService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserDto dto) {
        User user = authenticationService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto dto) {
        User user = authenticationService.authenticate(dto);
        String jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(jwtToken, jwtService.getJwt_expiration()));
    }

    @PostMapping("/verify")
    public ResponseEntity<UserResponse> verifyUser(@Valid @RequestBody VerifyUserDto dto) {
        User user = authenticationService.verifyUser(dto);
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @PostMapping("/resend")
    public ResponseEntity<Map<String, String>> resendVerificationCode(@Valid @RequestBody ResendVerificationDto dto) {
        authenticationService.resendVerificationCode(dto.email());
        return ResponseEntity.ok(Map.of("message", "Verification code sent"));
    }
}
