package com.ryde.rideservice.controller;

import com.ryde.rideservice.dto.RideRequest;
import com.ryde.rideservice.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService service;

    @PostMapping
    public ResponseEntity<?> createRide(Authentication auth, @RequestBody RideRequest request){
        Long riderId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(service.createRide(riderId, request));
    }
}