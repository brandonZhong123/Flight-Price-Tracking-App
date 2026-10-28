package com.example.flight_price_tracker.controller;

import com.example.flight_price_tracker.dto.LoginUserDto;
import com.example.flight_price_tracker.dto.RegisterUserDto;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.responses.LoginResponse;
import com.example.flight_price_tracker.service.AuthenticationService;
import com.example.flight_price_tracker.service.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/auth")
@Controller
public class AuthenticationController {

    private final JWTService jwtService;
    private final AuthenticationService authenticationService;


    public AuthenticationController(JWTService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto dto) {
        User registerUser = authenticationService.signup(dto);
        return ResponseEntity.ok(registerUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto dto) {
        User user = authenticationService.authenticate(dto);
        String jwtToken = jwtService.generateToken(user);
        LoginResponse res = new LoginResponse(jwtToken, jwtService.getJwt_expiration());
        return ResponseEntity.ok(res);
    }


}
