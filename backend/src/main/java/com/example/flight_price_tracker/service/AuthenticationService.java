package com.example.flight_price_tracker.service;

import com.example.flight_price_tracker.dto.LoginUserDto;
import com.example.flight_price_tracker.dto.RegisterUserDto;
import com.example.flight_price_tracker.dto.VerifyUserDto;
import com.example.flight_price_tracker.model.User;
import com.example.flight_price_tracker.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    private static final Duration CODE_TTL = Duration.ofMinutes(15);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @Transactional
    public User signup(RegisterUserDto dto) {
        String email = normalizeEmail(dto.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }
        if (userRepository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        User user = new User(dto.username(), email, passwordEncoder.encode(dto.password()));
        user.setEnabled(false);
        assignNewVerificationCode(user);
        User saved = userRepository.save(user);
        sendVerificationEmail(saved);
        return saved;
    }

    /**
     * Delegates to Spring Security, which checks the password and the enabled flag.
     * Throws BadCredentialsException or DisabledException on failure.
     */
    public User authenticate(LoginUserDto dto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizeEmail(dto.email()), dto.password()));
        return (User) auth.getPrincipal();
    }

    @Transactional
    public User verifyUser(VerifyUserDto dto) {
        User user = findByEmailOrThrow(dto.email());

        if (user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account is already verified");
        }
        if (user.getVerificationCode() == null || user.getVerificationCodeExpiresAt() == null
                || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Verification code has expired");
        }
        if (!codesMatch(user.getVerificationCode(), dto.verificationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification code");
        }

        user.setEnabled(true);
        user.setVerificationCode(null);
        user.setVerificationCodeExpiresAt(null);
        return userRepository.save(user);
    }

    @Transactional
    public void resendVerificationCode(String email) {
        User user = findByEmailOrThrow(email);
        if (user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account is already verified");
        }
        assignNewVerificationCode(user);
        userRepository.save(user);
        sendVerificationEmail(user);
    }

    private User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void assignNewVerificationCode(User user) {
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plus(CODE_TTL));
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account verification";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + user.getVerificationCode() + "</p>"
                + "<p style=\"font-size: 14px; color: #666;\">This code expires in " + CODE_TTL.toMinutes() + " minutes.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException | MailException e) {
            // Don't fail the request: the user can call /auth/resend once mail is working.
            log.error("Failed to send verification email to {}", user.getEmail(), e);
        }
    }

    private static boolean codesMatch(String expected, String provided) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                provided.getBytes(StandardCharsets.UTF_8));
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    // Uniform 6-digit code in [100000, 999999].
    private static String generateVerificationCode() {
        return String.valueOf(100000 + RANDOM.nextInt(900000));
    }
}
