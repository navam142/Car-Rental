package com.example.carrental.repository;

import com.example.carrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // All rentals for a specific user
    List<Rental> findByUserIdOrderByCreatedAtDesc(Long userId);

    // All rentals (admin) ordered by newest first
    List<Rental> findAllByOrderByCreatedAtDesc();

    // Check if a car has any overlapping active rentals in the requested date range
    @Query("""
            SELECT COUNT(r) > 0 FROM Rental r
            WHERE r.car.id = :carId
            AND r.status NOT IN ('CANCELLED', 'COMPLETED')
            AND r.startDate <= :endDate
            AND r.endDate >= :startDate
            """)
    boolean existsOverlappingRental(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}