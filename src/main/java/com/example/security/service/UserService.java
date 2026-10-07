package com.example.security.service;

import com.example.security.dto.RegisterRequest;
import com.example.security.dto.UserResponse;
import com.example.security.event.UserDeletedEvent;
import com.example.security.event.UserRegisteredEvent;
import com.example.security.event.UserRoleChangedEvent;
import com.example.security.exception.UserAlreadyExistsException;
import com.example.security.exception.UserNotFoundException;
import com.example.security.model.Role;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;
    private final UserCacheService userCacheService;

    @CacheEvict(value = {"users", "users-summary"}, allEntries = true)
    public void register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            log.warn("Registration failed - user already exists: {}", request.username());
            throw new UserAlreadyExistsException(request.username());
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .build();
        userRepository.save(user);
        log.info("New user registered: {}", request.username());
        eventPublisher.publishEvent(new UserRegisteredEvent(request.username(), "SYSTEM"));
    }

    public UserResponse getMe(String username) {
        log.info("Getting info for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        return toResponse(user);
    }

    public List<UserResponse> getUsers(String username, boolean isAdmin) {
        if (isAdmin) {
            log.info("ADMIN {} requested all users", username);
            return userCacheService.getAllUsers();
        } else {
            log.info("USER {} requested their own info", username);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException(username));
            return List.of(toResponse(user));
        }
    }

    public String getUsersSummary() {
        return userCacheService.getUsersSummary();
    }

    @CacheEvict(value = {"users", "users-summary"}, allEntries = true)
    public void changeRole(Long id, String newRole, String currentUserName) {
        Role role;
        try {
            role = Role.valueOf(newRole.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid role requested: {}", newRole);
            throw new IllegalArgumentException("Role does not exist: " + newRole);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.getUsername().equals(currentUserName)) {
            log.warn("Admin {} tried to change their own role", currentUserName);
            throw new IllegalArgumentException("Cannot change your own role");
        }
        user.setRole(role);
        userRepository.save(user);
        log.info("User {} role changed to {} by {}", user.getUsername(), newRole, currentUserName);
        eventPublisher.publishEvent(new UserRoleChangedEvent(user.getUsername(), newRole, currentUserName));
    }

    @CacheEvict(value = {"users", "users-summary"}, allEntries = true)
    public void deleteById(Long id, String currentUserName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        if (user.getUsername().equals(currentUserName)) {
            log.warn("Admin {} tried to delete themselves", currentUserName);
            throw new IllegalArgumentException("Cannot delete yourself");
        }
        userRepository.deleteById(id);
        log.info("User {} deleted by {}", user.getUsername(), currentUserName);
        eventPublisher.publishEvent(new UserDeletedEvent(user.getUsername(), currentUserName));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }
}
