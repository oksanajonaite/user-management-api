package com.example.security.controller;

import com.example.security.dto.RegisterRequest;
import com.example.security.exception.RateLimitExceededException;
import com.example.security.service.RateLimiterService;
import com.example.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RateLimiterService rateLimiterService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        String ip = httpRequest.getRemoteAddr();
        if (!rateLimiterService.tryConsume(ip)) {
            throw new RateLimitExceededException();
        }
        userService.register(request);
    }
}
