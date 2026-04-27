package com.example.carrental.dto.response;

import com.example.carrental.enums.LicenseStatus;
import com.example.carrental.enums.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String name;
    private boolean isVerified;
    private UserRole role;
    private String token;
    private String licenseNo;
    private LicenseStatus licenseStatus;
}
