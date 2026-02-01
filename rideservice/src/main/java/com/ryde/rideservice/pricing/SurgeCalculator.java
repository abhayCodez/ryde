package com.ryde.rideservice.pricing;

import org.springframework.stereotype.Component;

@Component
public class SurgeCalculator {

    public double getSurgeMultiplier(boolean enabled) {
        return enabled ? 1.5 : 1.0;
    }
}