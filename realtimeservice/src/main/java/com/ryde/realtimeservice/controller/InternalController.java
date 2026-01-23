package com.ryde.realtimeservice.controller;

import com.ryde.realtimeservice.dto.NearbyDriverResponse;
import com.ryde.realtimeservice.service.RealtimeLocationService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    @Value("${internal.key}")
    private String INTERNAL_KEY;
    private final RealtimeLocationService service;

    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearbyDriverResponse>> findNearbyDrivers(
            @RequestParam @DecimalMin("-90.0") @DecimalMax("90.0") double lat,
            @RequestParam @DecimalMin("-180.0") @DecimalMax("180.0") double lng,
            @RequestHeader("X-INTERNAL-KEY") String key
    ) {
        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(service.findNearbyDrivers(lat, lng));
    }
}