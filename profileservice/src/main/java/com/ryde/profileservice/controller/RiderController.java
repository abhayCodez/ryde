package com.ryde.profileservice.controller;

import com.ryde.profileservice.model.Rider;
import com.ryde.profileservice.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rider")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService service;

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        Rider rider = service.getRider(userId);
        return ResponseEntity.ok(rider);
    }
}