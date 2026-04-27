package com.example.carrental.controller;

import com.example.carrental.dto.request.ResendVerificationRequest;
import com.example.carrental.dto.request.UserLoginRequest;
import com.example.carrental.dto.request.UserRegisterRequest;
import com.example.carrental.dto.request.UserUpdateRequest;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegisterRequest registerRequest) {
        return ResponseEntity.status(201).body(userService.register(registerRequest));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @GetMapping("/auth/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/auth/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        userService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Verification email sent"));
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PutMapping("/users/me")
    public ResponseEntity<UserResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest updateRequest) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), updateRequest));
    }
}
