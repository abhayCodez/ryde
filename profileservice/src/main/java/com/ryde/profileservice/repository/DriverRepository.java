package com.ryde.profileservice.repository;

import com.ryde.profileservice.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByUserId(Long userId);
}