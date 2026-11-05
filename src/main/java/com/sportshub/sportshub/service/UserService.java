package com.sportshub.sportshub.service;

import com.sportshub.sportshub.dto.UpdateUserRequest;
import com.sportshub.sportshub.dto.UserResponse;
import com.sportshub.sportshub.model.User;
import com.sportshub.sportshub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse findById(Long id) {
        return UserResponse.from(getUserOrThrow(id));
    }

    public UserResponse update(Long id, UpdateUserRequest request, String currentUsername) {
        User user = getUserOrThrow(id);
        requireOwner(user, currentUsername);

        boolean emailChanged = !user.getEmail().equals(request.email());
        if (emailChanged && userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        user.setEmail(request.email());
        user.setBio(request.bio());
        return UserResponse.from(userRepository.save(user));
    }

    public void delete(Long id, String currentUsername) {
        User user = getUserOrThrow(id);
        requireOwner(user, currentUsername);
        userRepository.delete(user);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    // Authorization rule: you may only change your own account
    private void requireOwner(User user, String currentUsername) {
        if (!user.getUsername().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own account");
        }
    }
}