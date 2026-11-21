package com.ryde.rideservice.distance;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class HaversineDistanceCalculatorTest {

    private final HaversineDistanceCalculator calculator = new HaversineDistanceCalculator();

    @Test
    void returnsZeroForTheSamePoint() {
        DistanceResult result = calculator.calculate(26.8467, 80.9462, 26.8467, 80.9462);

        assertThat(result.getDistanceInKm()).isZero();
    }

    @Test
    void oneDegreeOfLatitudeIsAbout111Km() {
        DistanceResult result = calculator.calculate(0, 0, 1, 0);

        assertThat(result.getDistanceInKm()).isCloseTo(111.19, within(0.01));
    }

    @Test
    void distanceIsTheSameInBothDirections() {
        double there = calculator.calculate(26.85, 80.949, 26.87, 80.99).getDistanceInKm();
        double back = calculator.calculate(26.87, 80.99, 26.85, 80.949).getDistanceInKm();

        assertThat(there).isCloseTo(back, within(1e-9));
    }
}
