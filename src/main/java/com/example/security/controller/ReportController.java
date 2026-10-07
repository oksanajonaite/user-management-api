package com.example.security.controller;

import com.example.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final UserService userService;

    @GetMapping("/users-summary")
    public String usersSummary() {
        return userService.getUsersSummary();
    }
}
