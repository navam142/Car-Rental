package com.example.carrental.service;

import com.example.carrental.dto.request.CarRequest;
import com.example.carrental.dto.request.CarStatusUpdateRequest;
import com.example.carrental.dto.response.CarResponse;
import com.example.carrental.entity.Car;
import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.CarStatus;
import com.example.carrental.enums.FuelType;
import com.example.carrental.exceptions.DuplicateResourceException;
import com.example.carrental.exceptions.ResourceNotFoundException;
import com.example.carrental.mapper.CarMapper;
import com.example.carrental.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final ImageService imageService;

    // ─── Admin: Create a car ───────────────────────────────────────────────────

    public CarResponse createCar(CarRequest request, MultipartFile imageFile) {
        if (carRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new DuplicateResourceException("Car with plate number " + request.getPlateNumber() + " already exists");
        }

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Car image is required");
        }
        String imageUrl = imageService.uploadProductImage(imageFile);
        Car car = carMapper.toEntity(request);
        car.setImageUrl(imageUrl);
        return carMapper.toDto(carRepository.save(car));
    }

    // ─── Admin: Update a car ───────────────────────────────────────────────────

    public CarResponse updateCar(Long id, CarRequest request) {
        Car car = findCarById(id);

        // Allow plate number change only if it's not taken by another car
        if (!car.getPlateNumber().equals(request.getPlateNumber())
                && carRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new DuplicateResourceException("Car with plate number " + request.getPlateNumber() + " already exists");
        }

        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setColor(request.getColor());
        car.setPlateNumber(request.getPlateNumber());
        car.setSeatCount(request.getSeatCount());
        car.setPricePerDay(request.getPricePerDay());
        car.setImageUrl(request.getImageUrl());
        car.setFuelType(request.getFuelType());
        car.setCategory(request.getCategory());

        return carMapper.toDto(carRepository.save(car));
    }

    // ─── Admin: Delete a car ───────────────────────────────────────────────────

    public void deleteCar(Long id) {
        Car car = findCarById(id);
        carRepository.delete(car);
    }

    // ─── Admin: Update car status ──────────────────────────────────────────────

    public CarResponse updateCarStatus(Long id, CarStatusUpdateRequest request) {
        Car car = findCarById(id);
        car.setStatus(request.getStatus());
        return carMapper.toDto(carRepository.save(car));
    }

    // ─── Admin: Get any car by id ──────────────────────────────────────────────

    public CarResponse getCarById(Long id) {
        return carMapper.toDto(findCarById(id));
    }

    // ─── User: List available cars with optional filters ──────────────────────

    public List<CarResponse> getAvailableCars(CarCategory category, FuelType fuelType) {
        List<Car> cars;

        if (category != null && fuelType != null) {
            cars = carRepository.findByStatusAndCategoryAndFuelType(CarStatus.AVAILABLE, category, fuelType);
        } else if (category != null) {
            cars = carRepository.findByStatusAndCategory(CarStatus.AVAILABLE, category);
        } else if (fuelType != null) {
            cars = carRepository.findByStatusAndFuelType(CarStatus.AVAILABLE, fuelType);
        } else {
            cars = carRepository.findByStatus(CarStatus.AVAILABLE);
        }

        return cars.stream().map(carMapper::toDto).toList();
    }

    // ─── User: Check availability by date range ────────────────────────────────

    public List<CarResponse> getAvailableCarsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        return carRepository.findAvailableCarsByDateRange(startDate, endDate)
                .stream()
                .map(carMapper::toDto)
                .toList();
    }

    // ─── Internal helper ───────────────────────────────────────────────────────

    private Car findCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));
    }
}