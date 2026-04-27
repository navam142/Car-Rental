package com.example.carrental.dto.request;

import com.example.carrental.enums.LicenseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseUpdateRequest {

    @NotNull(message = "License status must not be null")
    private LicenseStatus licenseStatus;
}