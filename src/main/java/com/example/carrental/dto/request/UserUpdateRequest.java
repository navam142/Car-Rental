package com.example.carrental.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String email;

    private String name;
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    private String licenseNo;
}


