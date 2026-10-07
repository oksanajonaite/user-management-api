package com.example.security.controller;

import com.example.security.dto.UserResponse;
import com.example.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user/me")
    public UserResponse getMe(Authentication auth) {
        return userService.getMe(auth.getName());
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return userService.getUsers(auth.getName(), isAdmin);
    }
}
