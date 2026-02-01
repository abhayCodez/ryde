package com.ryde.rideservice.service.orchestration;

import com.ryde.rideservice.distance.DistanceCalculator;
import com.ryde.rideservice.distance.DistanceResult;
import com.ryde.rideservice.dto.RideQuote;
import com.ryde.rideservice.dto.RideRequest;
import com.ryde.rideservice.eta.EtaCalculator;
import com.ryde.rideservice.pricing.FareCalculator;
import com.ryde.rideservice.pricing.PricingResult;
import org.springframework.stereotype.Component;

@Component
public class RideRequestOrchestrator {

    private final DistanceCalculator distanceCalculator;
    private final FareCalculator fareCalculator;
    private final EtaCalculator etaCalculator;

    public RideRequestOrchestrator(
            DistanceCalculator distanceCalculator,
            FareCalculator fareCalculator,
            EtaCalculator etaCalculator) {

        this.distanceCalculator = distanceCalculator;
        this.fareCalculator = fareCalculator;
        this.etaCalculator = etaCalculator;
    }

    public RideQuote buildRideQuote(RideRequest request, boolean surge) {

        DistanceResult distance = distanceCalculator.calculate(
                request.getPickupLat(),
                request.getPickupLng(),
                request.getDropLat(),
                request.getDropLng()
        );

        PricingResult price = fareCalculator.calculateFare(
                distance,
                surge
        );

        int eta = etaCalculator.estimateMinutes(distance);

        return new RideQuote(distance, price, eta);
    }
}