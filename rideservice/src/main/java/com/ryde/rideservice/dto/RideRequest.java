package com.ryde.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideRequest {
    private double pickupLat;
    private double pickupLng;
    private double dropLat;
    private double dropLng;
}