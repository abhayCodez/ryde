package com.ryde.profileservice.repository;

import com.ryde.profileservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByUserId(Long userId);

    @Query("""
    select d from Driver d
    left join fetch d.vehicles
    where d.userId = :userId
    """)
    Optional<Driver> findWithVehiclesByUserId(Long userId);
}