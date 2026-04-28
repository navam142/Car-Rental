package com.example.carrental.controller;

import com.example.carrental.dto.response.CarResponse;
import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.FuelType;
import com.example.carrental.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    // GET /api/cars?category=SUV&fuelType=PETROL
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAvailableCars(
            @RequestParam(required = false) CarCategory category,
            @RequestParam(required = false) FuelType fuelType) {
        return ResponseEntity.ok(carService.getAvailableCars(category, fuelType));
    }

    // GET /api/cars/available?startDate=2025-06-01&endDate=2025-06-07
    @GetMapping("/available")
    public ResponseEntity<List<CarResponse>> getAvailableCarsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(carService.getAvailableCarsByDateRange(startDate, endDate));
    }

    // GET /api/cars/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }
}