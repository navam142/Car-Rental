package com.example.carrental.entity;

import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.CarStatus;
import com.example.carrental.enums.FuelType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false, unique = true)
    private String plateNumber;

    @Column(nullable = false)
    private Integer seatCount;

    @Column(nullable = false)
    private BigDecimal pricePerDay;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'AVAILABLE'")
    private CarStatus status = CarStatus.AVAILABLE;
}