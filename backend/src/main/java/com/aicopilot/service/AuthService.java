package com.aicopilot.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aicopilot.config.JwtService;
import com.aicopilot.dto.AuthRequest;
import com.aicopilot.dto.AuthResponse;
import com.aicopilot.dto.RegisterRequest;
import com.aicopilot.dto.UserDto;
import com.aicopilot.model.User;
import com.aicopilot.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with email " + email + " already exists.");
        }

        User user = new User(
                email,
                passwordEncoder.encode(request.getPassword()),
                request.getFullName(),
                request.getTargetRole() != null ? request.getTargetRole() : "Software Engineer",
                request.getYearsOfExperience()
        );

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getTargetRole(),
                savedUser.getYearsOfExperience()
        );
    }

    public AuthResponse login(AuthRequest request) {
        String email = normalizeEmail(request.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getTargetRole(),
                user.getYearsOfExperience()
        );
    }

    public UserDto getCurrentUser(String email) {
                User user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return new UserDto(user);
    }

        private String normalizeEmail(String email) {
                return email == null ? null : email.trim().toLowerCase(java.util.Locale.ROOT);
        }
}
