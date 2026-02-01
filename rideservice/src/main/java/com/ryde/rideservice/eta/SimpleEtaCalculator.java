package com.ryde.rideservice.eta;

import com.ryde.rideservice.distance.DistanceResult;
import org.springframework.stereotype.Component;

@Component
public class SimpleEtaCalculator implements EtaCalculator {

    @Override
    public int estimateMinutes(DistanceResult distance) {
        double avgSpeedKmPerHr = 30;
        return (int) Math.ceil(
                (distance.getDistanceInKm() / avgSpeedKmPerHr) * 60
        );
    }
}