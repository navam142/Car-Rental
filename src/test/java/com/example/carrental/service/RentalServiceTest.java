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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RentalMapper rentalMapper;

    @InjectMocks
    private RentalService rentalService;

    private static final String USER_EMAIL = "user@example.com";
    private static final Long CAR_ID = 7L;
    private static final Long USER_ID = 42L;
    private static final Long RENTAL_ID = 99L;

    private User approvedUser;
    private User pendingUser;
    private Car availableCar;
    private Car rentedCar;

    @BeforeEach
    void setUp() {
        approvedUser = user( USER_ID, USER_EMAIL, "Approved User", LicenseStatus.APPROVED);
        pendingUser = user( USER_ID, USER_EMAIL, "Pending User", LicenseStatus.PENDING);
        availableCar = car(CAR_ID, CarStatus.AVAILABLE, BigDecimal.valueOf(100));
        rentedCar = car(CAR_ID, CarStatus.RENTED, BigDecimal.valueOf(100));
    }

    @Test
    void bookCar_shouldCreateRentalWhenAllChecksPass() {
        RentalRequest request = RentalRequest.builder()
                .carId(CAR_ID)
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 5))
                .build();

        Rental persistedRental = rental(CAR_ID, USER_ID, request.getStartDate(), request.getEndDate(),
                BigDecimal.valueOf(400), RentalStatus.PENDING);
        persistedRental.setCar(availableCar);
        persistedRental.setUser(approvedUser);

        RentalResponse expectedResponse = RentalResponse.builder()
                .id(RENTAL_ID)
                .carId(CAR_ID)
                .userId(USER_ID)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalPrice(BigDecimal.valueOf(400))
                .status(RentalStatus.PENDING)
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(availableCar));
        when(rentalRepository.existsOverlappingRental(CAR_ID, request.getStartDate(), request.getEndDate())).thenReturn(false);
        when(rentalRepository.save(any(Rental.class))).thenReturn(persistedRental);
        when(rentalMapper.toDto(persistedRental)).thenReturn(expectedResponse);

        RentalResponse actual = rentalService.bookCar(USER_EMAIL, request);

        assertEquals(expectedResponse, actual);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());
        Rental savedRental = rentalCaptor.getValue();
        assertEquals(availableCar, savedRental.getCar());
        assertEquals(approvedUser, savedRental.getUser());
        assertEquals(request.getStartDate(), savedRental.getStartDate());
        assertEquals(request.getEndDate(), savedRental.getEndDate());
        assertEquals(RentalStatus.PENDING, savedRental.getStatus());
        assertEquals(0, BigDecimal.valueOf(400).compareTo(savedRental.getTotalPrice()));
        verify(rentalMapper).toDto(persistedRental);
        verify(rentalRepository).existsOverlappingRental(CAR_ID, request.getStartDate(), request.getEndDate());
    }

    @Test
    void bookCar_shouldRejectWhenLicenseIsNotApproved() {
        RentalRequest request = validRequest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(pendingUser));

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.bookCar(USER_EMAIL, request)
        );

        assertTrue(exception.getMessage().contains("license has not been approved"));
        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(carRepository, rentalRepository, rentalMapper);
    }

    @Test
    void bookCar_shouldRejectWhenCarIsUnavailable() {
        RentalRequest request = validRequest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(rentedCar));

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.bookCar(USER_EMAIL, request)
        );

        assertTrue(exception.getMessage().contains("Car is not available"));
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(carRepository).findById(CAR_ID);
        verify(rentalRepository, never()).existsOverlappingRental(any(), any(), any());
        verify(rentalRepository, never()).save(any());
        verify(rentalMapper, never()).toDto(any());
    }

    @Test
    void bookCar_shouldRejectWhenDateRangeIsInvalid() {
        RentalRequest request = RentalRequest.builder()
                .carId(CAR_ID)
                .startDate(LocalDate.of(2026, 5, 5))
                .endDate(LocalDate.of(2026, 5, 5))
                .build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(availableCar));

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.bookCar(USER_EMAIL, request)
        );

        assertTrue(exception.getMessage().contains("End date must be after start date"));
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(carRepository).findById(CAR_ID);
        verify(rentalRepository, never()).existsOverlappingRental(any(), any(), any());
    }

    @Test
    void bookCar_shouldRejectWhenDatesOverlapExistingRental() {
        RentalRequest request = validRequest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.of(availableCar));
        when(rentalRepository.existsOverlappingRental(CAR_ID, request.getStartDate(), request.getEndDate())).thenReturn(true);

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.bookCar(USER_EMAIL, request)
        );

        assertTrue(exception.getMessage().contains("already booked"));
        verify(rentalRepository).existsOverlappingRental(CAR_ID, request.getStartDate(), request.getEndDate());
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void bookCar_shouldThrowWhenUserDoesNotExist() {
        RentalRequest request = validRequest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> rentalService.bookCar(USER_EMAIL, request)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository).findByEmail(USER_EMAIL);
        verifyNoInteractions(carRepository, rentalRepository, rentalMapper);
    }

    @Test
    void bookCar_shouldThrowWhenCarDoesNotExist() {
        RentalRequest request = validRequest();
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(carRepository.findById(CAR_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> rentalService.bookCar(USER_EMAIL, request)
        );

        assertTrue(exception.getMessage().contains("Car not found"));
        verify(userRepository).findByEmail(USER_EMAIL);
        verify(carRepository).findById(CAR_ID);
        verifyNoInteractions(rentalRepository, rentalMapper);
    }

    @Test
    void getMyRentals_shouldReturnMappedRentalsForUser() {
        Rental first = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.CONFIRMED);
        first.setCar(availableCar);
        first.setUser(approvedUser);
        Rental second = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12), BigDecimal.valueOf(200), RentalStatus.PENDING);
        second.setCar(availableCar);
        second.setUser(approvedUser);

        RentalResponse firstResponse = RentalResponse.builder().id(1L).status(RentalStatus.CONFIRMED).build();
        RentalResponse secondResponse = RentalResponse.builder().id(2L).status(RentalStatus.PENDING).build();

        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(rentalRepository.findByUserIdOrderByCreatedAtDesc(USER_ID)).thenReturn(List.of(second, first));
        when(rentalMapper.toDto(second)).thenReturn(secondResponse);
        when(rentalMapper.toDto(first)).thenReturn(firstResponse);

        List<RentalResponse> responses = rentalService.getMyRentals(USER_EMAIL);

        assertEquals(List.of(secondResponse, firstResponse), responses);
        verify(rentalRepository).findByUserIdOrderByCreatedAtDesc(USER_ID);
        verify(rentalMapper).toDto(second);
        verify(rentalMapper).toDto(first);
    }

    @Test
    void getAllRentals_shouldReturnMappedRentals() {
        Rental first = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.CONFIRMED);
        first.setCar(availableCar);
        first.setUser(approvedUser);
        Rental second = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 10), LocalDate.of(2026, 5, 12), BigDecimal.valueOf(200), RentalStatus.PENDING);
        second.setCar(availableCar);
        second.setUser(approvedUser);

        RentalResponse firstResponse = RentalResponse.builder().id(1L).status(RentalStatus.CONFIRMED).build();
        RentalResponse secondResponse = RentalResponse.builder().id(2L).status(RentalStatus.PENDING).build();

        when(rentalRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(second, first));
        when(rentalMapper.toDto(second)).thenReturn(secondResponse);
        when(rentalMapper.toDto(first)).thenReturn(firstResponse);

        List<RentalResponse> responses = rentalService.getAllRentals();

        assertEquals(List.of(secondResponse, firstResponse), responses);
        verify(rentalRepository).findAllByOrderByCreatedAtDesc();
        verify(rentalMapper).toDto(second);
        verify(rentalMapper).toDto(first);
    }

    @Test
    void cancelRental_shouldCancelOwnPendingRental() {
        Rental rental = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.PENDING);
        rental.setId(RENTAL_ID);
        rental.setCar(availableCar);
        rental.setUser(approvedUser);

        Rental cancelledRental = rental(RENTAL_ID, USER_ID, rental.getStartDate(), rental.getEndDate(), rental.getTotalPrice(), RentalStatus.CANCELLED);
        cancelledRental.setCar(availableCar);
        cancelledRental.setUser(approvedUser);

        RentalResponse expectedResponse = RentalResponse.builder().id(RENTAL_ID).status(RentalStatus.CANCELLED).build();

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));
        when(rentalRepository.save(rental)).thenReturn(cancelledRental);
        when(rentalMapper.toDto(cancelledRental)).thenReturn(expectedResponse);

        RentalResponse actual = rentalService.cancelRental(USER_EMAIL, RENTAL_ID);

        assertEquals(expectedResponse, actual);
        assertEquals(RentalStatus.CANCELLED, rental.getStatus());
        verify(rentalRepository).save(rental);
    }

    @Test
    void cancelRental_shouldRejectWhenRentalBelongsToAnotherUser() {
        Rental rental = rental(CAR_ID, 777L, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.PENDING);
        rental.setId(RENTAL_ID);
        rental.setCar(availableCar);
        rental.setUser(user(777L, "other@example.com", "Other User", LicenseStatus.APPROVED));

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.cancelRental(USER_EMAIL, RENTAL_ID)
        );

        assertTrue(exception.getMessage().contains("not allowed to cancel"));
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void cancelRental_shouldRejectWhenRentalIsNotPending() {
        Rental rental = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.CONFIRMED);
        rental.setId(RENTAL_ID);
        rental.setCar(availableCar);
        rental.setUser(approvedUser);

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(userRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.of(approvedUser));

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.cancelRental(USER_EMAIL, RENTAL_ID)
        );

        assertTrue(exception.getMessage().contains("Only PENDING rentals can be cancelled"));
        verify(rentalRepository, never()).save(any());
    }

    @Test
    void cancelRental_shouldThrowWhenRentalIsMissing() {
        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> rentalService.cancelRental(USER_EMAIL, RENTAL_ID)
        );

        assertTrue(exception.getMessage().contains("Rental not found"));
        verifyNoInteractions(userRepository, rentalMapper);
    }

    @Test
    void updateRentalStatus_shouldAdvanceFromPendingToConfirmed() {
        Rental rental = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.PENDING);
        rental.setId(RENTAL_ID);
        rental.setCar(availableCar);
        rental.setUser(approvedUser);
        RentalStatusUpdateRequest request = RentalStatusUpdateRequest.builder().status(RentalStatus.CONFIRMED).build();

        Rental updatedRental = rental(RENTAL_ID, USER_ID, rental.getStartDate(), rental.getEndDate(), rental.getTotalPrice(), RentalStatus.CONFIRMED);
        updatedRental.setCar(availableCar);
        updatedRental.setUser(approvedUser);
        RentalResponse response = RentalResponse.builder().id(RENTAL_ID).status(RentalStatus.CONFIRMED).build();

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(updatedRental);
        when(rentalMapper.toDto(updatedRental)).thenReturn(response);

        RentalResponse actual = rentalService.updateRentalStatus(RENTAL_ID, request);

        assertEquals(response, actual);
        assertEquals(RentalStatus.CONFIRMED, rental.getStatus());
        verify(carRepository, never()).save(any());
    }

    @Test
    void updateRentalStatus_shouldMarkCarRentedWhenRentalBecomesActive() {
        Rental rental = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.CONFIRMED);
        rental.setId(RENTAL_ID);
        rental.setCar(availableCar);
        rental.setUser(approvedUser);
        RentalStatusUpdateRequest request = RentalStatusUpdateRequest.builder().status(RentalStatus.ACTIVE).build();

        Rental updatedRental = rental(RENTAL_ID, USER_ID, rental.getStartDate(), rental.getEndDate(), rental.getTotalPrice(), RentalStatus.ACTIVE);
        updatedRental.setCar(availableCar);
        updatedRental.setUser(approvedUser);
        RentalResponse response = RentalResponse.builder().id(RENTAL_ID).status(RentalStatus.ACTIVE).build();

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(carRepository.save(availableCar)).thenReturn(availableCar);
        when(rentalRepository.save(rental)).thenReturn(updatedRental);
        when(rentalMapper.toDto(updatedRental)).thenReturn(response);

        RentalResponse actual = rentalService.updateRentalStatus(RENTAL_ID, request);

        assertEquals(response, actual);
        assertEquals(CarStatus.RENTED, availableCar.getStatus());
        verify(carRepository).save(availableCar);
    }

    @Test
    void updateRentalStatus_shouldFreeCarWhenRentalCompletes() {
        Rental rental = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.ACTIVE);
        rental.setId(RENTAL_ID);
        rental.setCar(rentedCar);
        rental.setUser(approvedUser);
        RentalStatusUpdateRequest request = RentalStatusUpdateRequest.builder().status(RentalStatus.COMPLETED).build();

        Rental updatedRental = rental(RENTAL_ID, USER_ID, rental.getStartDate(), rental.getEndDate(), rental.getTotalPrice(), RentalStatus.COMPLETED);
        updatedRental.setCar(rentedCar);
        updatedRental.setUser(approvedUser);
        RentalResponse response = RentalResponse.builder().id(RENTAL_ID).status(RentalStatus.COMPLETED).build();

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));
        when(carRepository.save(rentedCar)).thenReturn(rentedCar);
        when(rentalRepository.save(rental)).thenReturn(updatedRental);
        when(rentalMapper.toDto(updatedRental)).thenReturn(response);

        RentalResponse actual = rentalService.updateRentalStatus(RENTAL_ID, request);

        assertEquals(response, actual);
        assertEquals(CarStatus.AVAILABLE, rentedCar.getStatus());
        verify(carRepository).save(rentedCar);
    }

    @Test
    void updateRentalStatus_shouldRejectInvalidTransition() {
        Rental rental = rental(CAR_ID, USER_ID, LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3), BigDecimal.valueOf(200), RentalStatus.PENDING);
        rental.setId(RENTAL_ID);
        rental.setCar(availableCar);
        rental.setUser(approvedUser);
        RentalStatusUpdateRequest request = RentalStatusUpdateRequest.builder().status(RentalStatus.ACTIVE).build();

        when(rentalRepository.findById(RENTAL_ID)).thenReturn(Optional.of(rental));

        InvalidRentalOperationException exception = assertThrows(
                InvalidRentalOperationException.class,
                () -> rentalService.updateRentalStatus(RENTAL_ID, request)
        );

        assertTrue(exception.getMessage().contains("Expected next status: CONFIRMED"));
        verify(rentalRepository).findById(RENTAL_ID);
        verifyNoInteractions(carRepository, rentalMapper);
    }

    private RentalRequest validRequest() {
        return RentalRequest.builder()
                .carId(CAR_ID)
                .startDate(LocalDate.of(2026, 5, 1))
                .endDate(LocalDate.of(2026, 5, 5))
                .build();
    }

    private User user(Long id, String email, String name, LicenseStatus licenseStatus) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setName(name);
        user.setLicenseStatus(licenseStatus);
        return user;
    }

    private Car car(Long id, CarStatus status, BigDecimal pricePerDay) {
        Car car = new Car();
        car.setId(id);
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setPlateNumber("ABC-123");
        car.setStatus(status);
        car.setPricePerDay(pricePerDay);
        return car;
    }

    private Rental rental(Long carId, Long userId, LocalDate startDate, LocalDate endDate, BigDecimal totalPrice, RentalStatus status) {
        Rental rental = new Rental();
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setTotalPrice(totalPrice);
        rental.setStatus(status);
        rental.setCar(car(carId, CarStatus.AVAILABLE, BigDecimal.valueOf(100)));
        rental.setUser(user(userId, "user@example.com", "User", LicenseStatus.APPROVED));
        return rental;
    }
}

