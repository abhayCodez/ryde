package com.ryde.rideservice.pricing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PricingResult {
    private double baseFare;
    private double distanceFare;
    private double surgeMultiplier;
    private double totalFare;
}