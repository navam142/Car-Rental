package com.example.carrental.dto.request;

import com.example.carrental.enums.CarStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private CarStatus status;
}