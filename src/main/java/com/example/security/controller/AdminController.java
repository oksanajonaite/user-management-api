package com.example.security.controller;

import com.example.security.dto.AuditLogResponse;
import com.example.security.service.AuditService;
import com.example.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuditService auditService;

    @PutMapping("/users/{id}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeRole(@PathVariable Long id, @RequestParam String role, Authentication auth) {
        userService.changeRole(id, role, auth.getName());
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id, Authentication auth) {
        userService.deleteById(id, auth.getName());
    }

    @GetMapping("/audit")
    public List<AuditLogResponse> getAuditLog() {
        return auditService.findAll();
    }
}
