package com.example.carrental.dto.response;

import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.FuelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicCarResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private Integer seatCount;
    private BigDecimal pricePerDay;
    private String imageUrl;
    private FuelType fuelType;
    private CarCategory category;
}
