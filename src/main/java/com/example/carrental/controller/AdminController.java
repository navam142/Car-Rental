package com.example.carrental.controller;

import com.example.carrental.dto.request.CarRequest;
import com.example.carrental.dto.request.CarStatusUpdateRequest;
import com.example.carrental.dto.request.LicenseUpdateRequest;
import com.example.carrental.dto.request.RentalStatusUpdateRequest;
import com.example.carrental.dto.response.CarResponse;
import com.example.carrental.dto.response.RentalResponse;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.service.CarService;
import com.example.carrental.service.RentalService;
import com.example.carrental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final CarService carService;
    private final RentalService rentalService;

    // --- user management ---

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

    // --- car management ---

    @PostMapping(path = "/cars", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarResponse> createCar(@Valid @RequestPart("car")  CarRequest carRequest, @RequestPart("image") MultipartFile image) {
        return ResponseEntity.status(201).body(carService.createCar(carRequest, image));
    }

    @PutMapping("/cars/{id}")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequest carRequest) {
        return ResponseEntity.ok(carService.updateCar(id, carRequest));
    }

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cars/{id}/status")
    public ResponseEntity<CarResponse> updateCarStatus(
            @PathVariable Long id,
            @Valid @RequestBody CarStatusUpdateRequest request) {
        return ResponseEntity.ok(carService.updateCarStatus(id, request));
    }

    // --- rental management ---
    @GetMapping("/rentals")
    public ResponseEntity<List<RentalResponse>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @PatchMapping("/rentals/{id}/status")
    public ResponseEntity<RentalResponse> updateRentalStatus(
            @PathVariable Long id,
            @Valid @RequestBody RentalStatusUpdateRequest request) {
        return ResponseEntity.ok(rentalService.updateRentalStatus(id, request));
    }
}