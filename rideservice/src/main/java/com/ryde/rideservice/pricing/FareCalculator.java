package com.ryde.rideservice.pricing;

import com.ryde.rideservice.distance.DistanceResult;

public interface FareCalculator {

    PricingResult calculateFare(
            DistanceResult distance,
            boolean surgeEnabled
    );
}