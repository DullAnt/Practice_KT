package com.courseplatform.userservice.controller;

import com.courseplatform.common.dto.AuthRequest;
import com.courseplatform.common.dto.AuthResponse;
import com.courseplatform.common.dto.RegisterRequest;
import com.courseplatform.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        log.info("Registration request for user: {}", request.getUsername());
        AuthResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        log.info("Login request for user: {}", request.getUsername());
        AuthResponse response = userService.authenticate(request);
        return ResponseEntity.ok(response);
    }
}
