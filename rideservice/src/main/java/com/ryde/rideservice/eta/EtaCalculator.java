package com.ryde.rideservice.eta;

import com.ryde.rideservice.distance.DistanceResult;

public interface EtaCalculator {
    int estimateMinutes(DistanceResult distance);
}