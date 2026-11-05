package com.sportshub.sportshub.service;

import com.sportshub.sportshub.dto.AuthResponse;
import com.sportshub.sportshub.dto.LoginRequest;
import com.sportshub.sportshub.dto.RegisterRequest;
import com.sportshub.sportshub.dto.UserResponse;
import com.sportshub.sportshub.model.User;
import com.sportshub.sportshub.repository.UserRepository;
import com.sportshub.sportshub.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        String hash = passwordEncoder.encode(request.password());
        User saved = userRepository.save(
                new User(request.username(), request.email(), request.bio(), hash));
        return UserResponse.from(saved);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid username or password"));

        return new AuthResponse(jwtService.generateToken(user.getUsername()));
    }
}