package com.example.carrental.mapper;

import com.example.carrental.dto.request.UserRegisterRequest;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toEntity(UserRegisterRequest registerRequest) {
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setName(registerRequest.getName());
        user.setLicenseNo(registerRequest.getLicenseNo());
        return user;
    }

    public UserResponse toDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isVerified(user.isVerified())
                .licenseNo(user.getLicenseNo())
                .role(user.getRole())
                .licenseStatus(user.getLicenseStatus())
                .build();
    }
}
