package com.example.carrental.service;

import com.example.carrental.dto.request.LicenseUpdateRequest;
import com.example.carrental.dto.request.UserLoginRequest;
import com.example.carrental.dto.request.UserRegisterRequest;
import com.example.carrental.dto.request.UserUpdateRequest;
import com.example.carrental.dto.response.UserResponse;
import com.example.carrental.entity.User;
import com.example.carrental.exceptions.DuplicateResourceException;
import com.example.carrental.exceptions.EmailNotVerifiedException;
import com.example.carrental.exceptions.InvalidCredentialsException;
import com.example.carrental.exceptions.ResourceNotFoundException;
import com.example.carrental.exceptions.VerificationTokenException;
import com.example.carrental.mapper.UserMapper;
import com.example.carrental.repository.UserRepository;
import com.example.carrental.security.CustomUserDetails;
import com.example.carrental.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationMailService verificationMailService;

    @Value("${application.mail.verification-token-expiration-minutes}")
    private long verificationTokenExpirationMinutes;

    @Transactional
    public UserResponse register(UserRegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException(registerRequest.getEmail() + " is already registered");
        }

        // Create and save the user
        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        prepareVerificationToken(user);
        User savedUser = userRepository.save(user);
        verificationMailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getVerificationToken());
        return userMapper.toDto(savedUser);
    }

    public UserResponse login(UserLoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginRequest.getEmail()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        if (!user.isVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in");
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

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new VerificationTokenException("Invalid verification token"));

        if (user.isVerified()) {
            return;
        }

        if (user.getVerificationTokenExpiry() == null || user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new VerificationTokenException("Verification token has expired");
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isVerified()) {
            throw new VerificationTokenException("Email is already verified");
        }

        prepareVerificationToken(user);
        User updatedUser = userRepository.save(user);
        verificationMailService.sendVerificationEmail(updatedUser.getEmail(), updatedUser.getVerificationToken());
    }

    // ─── Admin: Approve or reject a user's license ────────────────────────────

    public UserResponse updateLicenseStatus(Long userId, LicenseUpdateRequest licenseUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setLicenseStatus(licenseUpdateRequest.getLicenseStatus());
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    private void prepareVerificationToken(User user) {
        user.setVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(verificationTokenExpirationMinutes));
    }
}