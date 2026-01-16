package com.ryde.profileservice.controller;

import com.ryde.profileservice.model.Driver;
import com.ryde.profileservice.model.Vehicle;
import com.ryde.profileservice.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @PostMapping("/license")
    public ResponseEntity<?> addLicense(Authentication auth,
                                        @RequestBody Map<String, String> body) {
        try {
            Long userId = Long.parseLong(auth.getName());
            service.addLicense(userId, body.get("license"));
            return ResponseEntity.ok(Map.of("message", "License added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/vehicles")
    public ResponseEntity<?> addVehicle(Authentication auth,
                                        @RequestBody Vehicle vehicle) {
        try {
            Long userId = Long.parseLong(auth.getName());
            service.addVehicle(userId, vehicle);
            return ResponseEntity.ok(Map.of("message", "Vehicle added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/availability")
    public ResponseEntity<?> availability(Authentication auth,
                                          @RequestParam boolean available) {
        try {
            Long userId = Long.parseLong(auth.getName());
            Driver updated = service.updateAvailability(userId, available);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}