package com.example.carrental.dto.response;

import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.CarStatus;
import com.example.carrental.enums.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String plateNumber;
    private Integer seatCount;
    private BigDecimal pricePerDay;
    private String imageUrl;
    private FuelType fuelType;
    private CarCategory category;
    private CarStatus status;
}