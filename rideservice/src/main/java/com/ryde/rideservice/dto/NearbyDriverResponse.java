package com.ryde.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NearbyDriverResponse {
    private Long driverId;
    private double distanceKm;
}