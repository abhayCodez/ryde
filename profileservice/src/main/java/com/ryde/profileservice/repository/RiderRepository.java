package com.ryde.profileservice.repository;

import com.ryde.profileservice.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    Optional<Rider> findByUserId(Long userId);
}