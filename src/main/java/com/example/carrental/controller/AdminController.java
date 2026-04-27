package com.example.carrental.controller;

import com.example.carrental.dto.request.LicenseUpdateRequest;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{id}/license")
    public ResponseEntity<UserResponse> updateLicenseStatus(
            @PathVariable Long id,
            @Valid @RequestBody LicenseUpdateRequest licenseUpdateRequest) {
        return ResponseEntity.ok(userService.updateLicenseStatus(id, licenseUpdateRequest));
    }
}