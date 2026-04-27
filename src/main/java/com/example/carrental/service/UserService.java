package com.example.carrental.service;

import com.example.carrental.dto.request.LicenseUpdateRequest;
import com.example.carrental.dto.request.UserLoginRequest;
import com.example.carrental.dto.request.UserRegisterRequest;
import com.example.carrental.dto.request.UserUpdateRequest;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.entity.User;
import com.example.carrental.exceptions.DuplicateResourceException;
import com.example.carrental.exceptions.InvalidCredentialsException;
import com.example.carrental.exceptions.ResourceNotFoundException;
import com.example.carrental.mapper.UserMapper;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.security.CustomUserDetails;
import com.example.carrental.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserResponse register(UserRegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException(registerRequest.getEmail() + " is already registered");
        }

        // Create and save the user
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public UserResponse login(UserLoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(CustomUserDetails.from(user));
        UserResponse response = userMapper.toDto(user);
        response.setToken(token);
        return response;

    }

    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    public UserResponse updateProfile(String email, UserUpdateRequest updateRequest) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (updateRequest.getName() != null && !updateRequest.getName().isBlank()) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        if (updateRequest.getLicenseNo() != null && !updateRequest.getLicenseNo().isBlank()) {
            user.setLicenseNo(updateRequest.getLicenseNo());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    // ─── Admin: List all users ─────────────────────────────────────────────────

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    // ─── Admin: Approve or reject a user's license ────────────────────────────

    public UserResponse updateLicenseStatus(Long userId, LicenseUpdateRequest licenseUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setLicenseStatus(licenseUpdateRequest.getLicenseStatus());
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }
}