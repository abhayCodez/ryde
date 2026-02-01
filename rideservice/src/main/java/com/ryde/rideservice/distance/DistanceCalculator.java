package com.ryde.rideservice.distance;

public interface DistanceCalculator {

    DistanceResult calculate(
            double pickupLat,
            double pickupLng,
            double dropLat,
            double dropLng
    );
}