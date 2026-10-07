package com.example.security.dto;

import com.example.security.model.Role;

public record UserResponse(
        Long id,
        String username,
        Role role,
        String email,
        String phoneNumber

) {
}
