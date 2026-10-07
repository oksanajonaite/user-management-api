package com.example.security.service;

import com.example.security.dto.UserResponse;
import com.example.security.model.Role;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCacheService {

    private final UserRepository userRepository;

    @Cacheable("users")
    public List<UserResponse> getAllUsers() {
        log.info("Fetching ALL users from DB");
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getEmail(),
                        user.getPhoneNumber()
                )).toList();
    }

    @Cacheable("users-summary")
    public String getUsersSummary() {
        log.info("Fetching users summary from DB");
        var all = userRepository.findAll();
        Map<Role, Long> counts = all.stream()
                .collect(Collectors.groupingBy(user -> user.getRole(), Collectors.counting()));

        long total = all.size();
        long admins = counts.getOrDefault(Role.ADMIN, 0L);
        long managers = counts.getOrDefault(Role.MANAGER, 0L);
        long users = counts.getOrDefault(Role.USER, 0L);

        return "Total: " + total +
                ", ADMIN: " + admins +
                ", MANAGER: " + managers +
                ", USER: " + users;
    }
}
