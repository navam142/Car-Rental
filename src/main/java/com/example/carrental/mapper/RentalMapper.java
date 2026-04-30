package com.example.carrental.mapper;

import com.example.carrental.dto.response.RentalResponse;
import com.example.carrental.entity.Rental;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    public RentalResponse toDto(Rental rental) {
        return RentalResponse.builder()
                .id(rental.getId())
                .carId(rental.getCar().getId())
                .carBrand(rental.getCar().getBrand())
                .carModel(rental.getCar().getModel())
                .carPlateNumber(rental.getCar().getPlateNumber())
                .userId(rental.getUser().getId())
                .userName(rental.getUser().getName())
                .startDate(rental.getStartDate())
                .endDate(rental.getEndDate())
                .totalPrice(rental.getTotalPrice())
                .status(rental.getStatus())
                .createdAt(rental.getCreatedAt())
                .build();
    }
}