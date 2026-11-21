package com.ryde.rideservice.eta;

import com.ryde.rideservice.distance.DistanceResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEtaCalculatorTest {

    private final SimpleEtaCalculator calculator = new SimpleEtaCalculator();

    @Test
    void fifteenKmAtThirtyKmPerHourTakesThirtyMinutes() {
        assertThat(calculator.estimateMinutes(new DistanceResult(15))).isEqualTo(30);
    }

    @Test
    void sevenAndAHalfKmTakesFifteenMinutes() {
        assertThat(calculator.estimateMinutes(new DistanceResult(7.5))).isEqualTo(15);
    }

    @Test
    void roundsPartialMinutesUp() {
        assertThat(calculator.estimateMinutes(new DistanceResult(0.1))).isEqualTo(1);
    }
}
