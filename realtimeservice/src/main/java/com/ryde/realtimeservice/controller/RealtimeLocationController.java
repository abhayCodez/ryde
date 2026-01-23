package com.ryde.realtimeservice.controller;

import com.ryde.realtimeservice.dto.DriverStatus;
import com.ryde.realtimeservice.dto.LocationUpdateRequest;
import com.ryde.realtimeservice.dto.NearbyDriverResponse;
import com.ryde.realtimeservice.service.RealtimeLocationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/location")
public class RealtimeLocationController {

    private final RealtimeLocationService service;

    public RealtimeLocationController(RealtimeLocationService service) {
        this.service = service;
    }

    @PostMapping("/drivers/online")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> driverOnline(Authentication auth) {
        System.out.println("CONTROLLER HIT");
        Long userId = (Long) auth.getPrincipal();
        DriverStatus status = service.driverOnline(userId);
        return switch (status) {
            case ONLINE -> ResponseEntity.ok(
                    Map.of("driverId", userId, "status", "ONLINE")
            );
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @PostMapping("/drivers/offline")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> driverOffline(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        DriverStatus status = service.driverOffline(userId);
        return switch (status) {
            case OFFLINE -> ResponseEntity.ok(
                    Map.of("driverId", userId, "status", "OFFLINE")
            );
            default -> ResponseEntity.internalServerError().build();
        };
    }

    @PostMapping("/drivers/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> updateDriverLocation(Authentication auth, @RequestBody LocationUpdateRequest req) {
        Long userId = (Long) auth.getPrincipal();
        service.updateDriverLocation(userId, req.getLatitude(), req.getLongitude());
        return ResponseEntity.ok(
                Map.of(
                        "driverId", userId,
                        "lat", req.getLatitude(),
                        "lng", req.getLongitude(),
                        "message", "Location updated"
                )
        );
    }

    @PostMapping("/riders/location")
    @PreAuthorize("hasRole('RIDER')")
    public ResponseEntity<?> updateRiderLocation(Authentication auth, @Valid @RequestBody LocationUpdateRequest req) {
        Long userId = (Long) auth.getPrincipal();
        service.updateRiderLocation(userId, req.getLatitude(), req.getLongitude());
        return ResponseEntity.ok(
                Map.of(
                        "riderId", userId,
                        "message", "Location updated"
                )
        );
    }
}