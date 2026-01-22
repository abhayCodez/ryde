package com.ryde.profileservice.controller;

import com.ryde.profileservice.dto.DriverProfileResponse;
import com.ryde.profileservice.dto.TokenResponse;
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
            Long userId = (Long) auth.getPrincipal();
            TokenResponse token = service.addLicense(userId, body.get("license"));
            if(token != null){
                return ResponseEntity.ok(Map.of(
                        "message", "License added and role upgraded to driver",
                        "new access token", token.getNewAccessToken()
                ));
            }
            return ResponseEntity.ok(Map.of("message", "License added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/vehicles")
    public ResponseEntity<?> addVehicle(Authentication auth,
                                        @RequestBody Vehicle vehicle) {
        try {
            Long userId = (Long) auth.getPrincipal();
            TokenResponse token = service.addVehicle(userId, vehicle);
            if(token != null){
                return ResponseEntity.ok(Map.of(
                        "message", "Vehicle added and role upgraded to driver",
                        "new access token", token.getNewAccessToken()
                ));
            }
            return ResponseEntity.ok(Map.of("message", "Vehicle added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<DriverProfileResponse> getMyProfile(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(
                service.getDriverProfile(userId)
        );
    }
}