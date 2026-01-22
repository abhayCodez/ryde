package com.ryde.profileservice.service;

import com.ryde.profileservice.client.AuthServiceClient;
import com.ryde.profileservice.dto.DriverProfileResponse;
import com.ryde.profileservice.dto.TokenResponse;
import com.ryde.profileservice.dto.VehicleResponse;
import com.ryde.profileservice.model.Driver;
import com.ryde.profileservice.model.Vehicle;
import com.ryde.profileservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverService {

    private final DriverRepository repository;
    private final AuthServiceClient authClient;

    public TokenResponse addLicense(Long userId, String license) {
        Driver driver = repository.findByUserId(userId)
                .orElseGet(() -> {
                    Driver d = new Driver();
                    d.setUserId(userId);
                    return d;
                });

        driver.setLicenseNumber(license);
        repository.save(driver);
        Driver fresh = repository.findByUserId(userId)
                .orElseThrow();
        return checkAndUpgrade(fresh);
    }

    public TokenResponse addVehicle(Long userId, Vehicle vehicle) {
        Driver driver = repository.findByUserId(userId)
                .orElseGet(() -> {
                    Driver d = new Driver();
                    d.setUserId(userId);
                    return d;
                });

        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);
        repository.save(driver);
        Driver fresh = repository.findWithVehiclesByUserId(userId)
                .orElseThrow();
        return checkAndUpgrade(fresh);
    }

    private TokenResponse checkAndUpgrade(Driver driver) {
        if (driver.isEligible()) {
            try {
                System.out.println("Calling auth-service for userId = " + driver.getUserId());

                TokenResponse token = authClient.upgradeToDriver(driver.getUserId());

                System.out.println("Token received = " + token);

                return token;
            } catch (Exception e) {
                e.printStackTrace(); // IMPORTANT
                throw new RuntimeException("Failed to upgrade role in auth-service");
            }
        }
        return null;
    }

    public DriverProfileResponse getDriverProfile(Long userId) {
        Driver driver = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));
        List<VehicleResponse> vehicles = driver.getVehicles()
                .stream()
                .map(v -> new VehicleResponse(v.getId(), v.getModel()))
                .toList();
        return new DriverProfileResponse(
                driver.getUserId(),
                driver.getLicenseNumber(),
                vehicles
        );
    }
}