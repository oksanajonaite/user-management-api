package com.example.security.dto;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        String actor,
        String action,
        String target,
        LocalDateTime timestamp
) {
}
