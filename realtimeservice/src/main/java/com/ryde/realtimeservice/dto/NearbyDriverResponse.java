package com.ryde.realtimeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class NearbyDriverResponse {
    private Long driverId;
    private double distanceKm;
}