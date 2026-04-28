package com.example.carrental.dto.request;

import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.FuelType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRequest {

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1990, message = "Year must be 1990 or later")
    @Max(value = 2100, message = "Year seems invalid")
    private Integer year;

    @NotBlank(message = "Color is required")
    private String color;

    @NotBlank(message = "Plate number is required")
    private String plateNumber;

    @NotNull(message = "Seat count is required")
    @Min(value = 1, message = "Seat count must be at least 1")
    @Max(value = 20, message = "Seat count seems too high")
    private Integer seatCount;

    @NotNull(message = "Price per day is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal pricePerDay;

    private String imageUrl;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    @NotNull(message = "Category is required")
    private CarCategory category;
}