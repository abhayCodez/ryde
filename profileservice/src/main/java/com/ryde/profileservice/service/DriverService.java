package com.ryde.profileservice.service;

import com.ryde.profileservice.client.AuthServiceClient;
import com.ryde.profileservice.model.Driver;
import com.ryde.profileservice.model.Vehicle;
import com.ryde.profileservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository repository;
    private final AuthServiceClient authClient;

    public Driver updateAvailability(Long userId, boolean available) {
        Driver driver = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setIsAvailable(available);
        return repository.save(driver);
    }

    public void addLicense(Long userId, String license) {
        Driver driver = repository.findByUserId(userId)
                .orElseGet(() -> {
                    Driver d = new Driver();
                    d.setUserId(userId);
                    return d;
                });

        driver.setLicenseNumber(license);
        repository.save(driver);
        checkAndUpgrade(driver);
    }

    public void addVehicle(Long userId, Vehicle vehicle) {
        Driver driver = repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        vehicle.setDriver(driver);
        driver.getVehicles().add(vehicle);
        repository.save(driver);
        checkAndUpgrade(driver);
    }

    private void checkAndUpgrade(Driver driver) {
        if (driver.isEligible()) {
            try {
                authClient.upgradeToDriver(driver.getUserId());
            } catch (Exception e) {
                throw new RuntimeException("Failed to upgrade role in auth-service");
            }
        }
    }
}