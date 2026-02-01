package com.ryde.rideservice.dto;

import com.ryde.rideservice.distance.DistanceResult;
import com.ryde.rideservice.pricing.PricingResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideQuote {

    private DistanceResult distance;
    private PricingResult price;
    private int eta;
}