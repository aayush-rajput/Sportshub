package com.sportshub.sportshub.dto;

import com.sportshub.sportshub.model.User;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String username,
        String email,
        String bio,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getCreatedAt()
        );
    }
}