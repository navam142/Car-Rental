package com.example.carrental.dto.response;

import com.example.carrental.enums.RentalStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalResponse {
    private Long id;
    private Long carId;
    private String carBrand;
    private String carModel;
    private String carPlateNumber;
    private Long userId;
    private String userName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private RentalStatus status;
    private LocalDateTime createdAt;
}