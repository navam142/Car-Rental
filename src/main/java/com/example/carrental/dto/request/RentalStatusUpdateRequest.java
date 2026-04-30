package com.example.carrental.dto.request;

import com.example.carrental.enums.RentalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RentalStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private RentalStatus status;
}