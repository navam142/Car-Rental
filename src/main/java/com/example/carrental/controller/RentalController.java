package com.example.carrental.controller;

import com.example.carrental.dto.request.RentalRequest;
import com.example.carrental.dto.response.RentalResponse;
import com.example.carrental.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    // POST /api/rentals — book a car
    @PostMapping
    public ResponseEntity<RentalResponse> bookCar(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RentalRequest request) {
        return ResponseEntity.status(201).body(rentalService.bookCar(userDetails.getUsername(), request));
    }

    // GET /api/rentals/my — view own rentals
    @GetMapping("/my")
    public ResponseEntity<List<RentalResponse>> getMyRentals(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(rentalService.getMyRentals(userDetails.getUsername()));
    }

    // DELETE /api/rentals/{id} — cancel a rental (only PENDING)
    @DeleteMapping("/{id}")
    public ResponseEntity<RentalResponse> cancelRental(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelRental(userDetails.getUsername(), id));
    }
}