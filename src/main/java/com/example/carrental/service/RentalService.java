package com.example.carrental.service;

import com.example.carrental.dto.request.RentalRequest;
import com.example.carrental.dto.request.RentalStatusUpdateRequest;
import com.example.carrental.dto.response.RentalResponse;
import com.example.carrental.entity.Car;
import com.example.carrental.entity.Rental;
import com.example.carrental.entity.User;
import com.example.carrental.enums.CarStatus;
import com.example.carrental.enums.LicenseStatus;
import com.example.carrental.enums.RentalStatus;
import com.example.carrental.exceptions.InvalidRentalOperationException;
import com.example.carrental.exceptions.ResourceNotFoundException;
import com.example.carrental.mapper.RentalMapper;
import com.example.carrental.repository.CarRepository;
import com.example.carrental.repository.RentalRepository;
import com.example.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final RentalMapper rentalMapper;

    // Valid status transitions: each status maps to what admin can move it to
    private static final Map<RentalStatus, RentalStatus> ALLOWED_TRANSITIONS = Map.of(
            RentalStatus.PENDING,   RentalStatus.CONFIRMED,
            RentalStatus.CONFIRMED, RentalStatus.ACTIVE,
            RentalStatus.ACTIVE,    RentalStatus.COMPLETED
    );

    // ─── User: Book a car ──────────────────────────────────────────────────────

    @Transactional
    public RentalResponse bookCar(String userEmail, RentalRequest request) {
        User user = findUserByEmail(userEmail);

        // License must be approved before renting
        if (user.getLicenseStatus() != LicenseStatus.APPROVED) {
            throw new InvalidRentalOperationException("Your license has not been approved yet. You cannot book a car.");
        }

        Car car = findCarById(request.getCarId());

        // Car must be available
        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new InvalidRentalOperationException("Car is not available for booking.");
        }

        // Dates must be valid
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new InvalidRentalOperationException("End date must be after start date.");
        }

        // No overlapping rentals for this car
        if (rentalRepository.existsOverlappingRental(car.getId(), request.getStartDate(), request.getEndDate())) {
            throw new InvalidRentalOperationException("Car is already booked for the selected dates.");
        }

        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigDecimal totalPrice = car.getPricePerDay().multiply(BigDecimal.valueOf(days));

        Rental rental = Rental.builder()
                .car(car)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalPrice(totalPrice)
                .status(RentalStatus.PENDING)
                .build();

        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    // ─── User: View own rentals ────────────────────────────────────────────────

    public List<RentalResponse> getMyRentals(String userEmail) {
        User user = findUserByEmail(userEmail);
        return rentalRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    // ─── User: Cancel a rental ─────────────────────────────────────────────────

    @Transactional
    public RentalResponse cancelRental(String userEmail, Long rentalId) {
        Rental rental = findRentalById(rentalId);
        User user = findUserByEmail(userEmail);

        // Users can only cancel their own rentals
        if (!rental.getUser().getId().equals(user.getId())) {
            throw new InvalidRentalOperationException("You are not allowed to cancel this rental.");
        }

        // Only PENDING rentals can be cancelled by the user
        if (rental.getStatus() != RentalStatus.PENDING) {
            throw new InvalidRentalOperationException(
                    "Only PENDING rentals can be cancelled. Current status: " + rental.getStatus());
        }

        rental.setStatus(RentalStatus.CANCELLED);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    // ─── Admin: View all rentals ───────────────────────────────────────────────

    public List<RentalResponse> getAllRentals() {
        return rentalRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    // ─── Admin: Advance rental status ─────────────────────────────────────────

    @Transactional
    public RentalResponse updateRentalStatus(Long rentalId, RentalStatusUpdateRequest request) {
        Rental rental = findRentalById(rentalId);
        RentalStatus currentStatus = rental.getStatus();
        RentalStatus requestedStatus = request.getStatus();

        RentalStatus expectedNext = ALLOWED_TRANSITIONS.get(currentStatus);

        // Only allow the exact next step in the flow; CANCELLED is not reachable by admin here
        if (expectedNext == null || !expectedNext.equals(requestedStatus)) {
            throw new InvalidRentalOperationException(
                    "Cannot transition from " + currentStatus + " to " + requestedStatus +
                            ". Expected next status: " + (expectedNext != null ? expectedNext : "none (terminal state)"));
        }

        // When rental goes ACTIVE, mark the car as RENTED
        if (requestedStatus == RentalStatus.ACTIVE) {
            rental.getCar().setStatus(CarStatus.RENTED);
            carRepository.save(rental.getCar());
        }

        // When rental COMPLETES, free up the car
        if (requestedStatus == RentalStatus.COMPLETED) {
            rental.getCar().setStatus(CarStatus.AVAILABLE);
            carRepository.save(rental.getCar());
        }

        rental.setStatus(requestedStatus);
        return rentalMapper.toDto(rentalRepository.save(rental));
    }

    // ─── Internal helpers ──────────────────────────────────────────────────────

    private Rental findRentalById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rental not found with id: " + id));
    }

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}