package com.example.carrental.mapper;

import com.example.carrental.dto.request.CarRequest;
import com.example.carrental.dto.response.CarResponse;
import com.example.carrental.entity.Car;
import com.example.carrental.enums.CarStatus;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    public Car toEntity(CarRequest request) {
        return Car.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .color(request.getColor())
                .plateNumber(request.getPlateNumber())
                .seatCount(request.getSeatCount())
                .pricePerDay(request.getPricePerDay())
                .imageUrl(request.getImageUrl())
                .fuelType(request.getFuelType())
                .category(request.getCategory())
                .status(CarStatus.AVAILABLE)
                .build();
    }

    public CarResponse toDto(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .color(car.getColor())
                .plateNumber(car.getPlateNumber())
                .seatCount(car.getSeatCount())
                .pricePerDay(car.getPricePerDay())
                .imageUrl(car.getImageUrl())
                .fuelType(car.getFuelType())
                .category(car.getCategory())
                .status(car.getStatus())
                .build();
    }
}