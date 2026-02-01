package com.ryde.rideservice.pricing;

import com.ryde.rideservice.distance.DistanceResult;
import org.springframework.stereotype.Component;

@Component
public class DefaultFareCalculator implements FareCalculator {

    private final SurgeCalculator surgeCalculator;

    public DefaultFareCalculator(SurgeCalculator surgeCalculator) {
        this.surgeCalculator = surgeCalculator;
    }

    @Override
    public PricingResult calculateFare(
            DistanceResult distance,
            boolean surgeEnabled) {

        double baseFare = 50;
        double perKmRate = 15;

        double distanceFare = distance.getDistanceInKm() * perKmRate;
        double surge = surgeCalculator.getSurgeMultiplier(surgeEnabled);

        double total = (baseFare + distanceFare) * surge;

        return new PricingResult(
                baseFare,
                distanceFare,
                surge,
                Math.round(total)
        );
    }
}