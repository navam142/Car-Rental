package com.example.carrental.controller;

import com.example.carrental.dto.request.UserLoginRequest;
import com.example.carrental.dto.request.UserRegisterRequest;
import com.example.carrental.dto.request.UserUpdateRequest;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.entity.User;
import com.example.carrental.mapper.UserMapper;
import com.example.carrental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest registerRequest) {
        return ResponseEntity.status(201).body(userService.register(registerRequest));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
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
