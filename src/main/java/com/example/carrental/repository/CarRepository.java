package com.example.carrental.repository;

import com.example.carrental.entity.Car;
import com.example.carrental.enums.CarCategory;
import com.example.carrental.enums.CarStatus;
import com.example.carrental.enums.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    boolean existsByPlateNumber(String plateNumber);

    // Filter available cars by optional category and fuel type
    List<Car> findByStatusAndCategoryAndFuelType(CarStatus status, CarCategory category, FuelType fuelType);
    List<Car> findByStatusAndCategory(CarStatus status, CarCategory category);
    List<Car> findByStatusAndFuelType(CarStatus status, FuelType fuelType);
    List<Car> findByStatus(CarStatus status);

    // Cars NOT booked in the given date range
    @Query("""
            SELECT c FROM Car c
            WHERE c.status = 'AVAILABLE'
            AND c.id NOT IN (
                SELECT r.car.id FROM Rental r
                WHERE r.startDate <= :endDate
                AND r.endDate >= :startDate
            )
            """)
    List<Car> findAvailableCarsByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}