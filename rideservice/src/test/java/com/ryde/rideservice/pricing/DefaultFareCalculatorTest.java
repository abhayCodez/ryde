package com.ryde.rideservice.pricing;

import com.ryde.rideservice.distance.DistanceResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultFareCalculatorTest {

    @Mock
    private SurgeCalculator surgeCalculator;

    @InjectMocks
    private DefaultFareCalculator fareCalculator;

    @Test
    void chargesBaseFarePlusPerKmRateWithoutSurge() {
        when(surgeCalculator.getSurgeMultiplier(false)).thenReturn(1.0);

        PricingResult result = fareCalculator.calculateFare(new DistanceResult(10), false);

        assertThat(result.getBaseFare()).isEqualTo(50);
        assertThat(result.getDistanceFare()).isEqualTo(150);
        assertThat(result.getSurgeMultiplier()).isEqualTo(1.0);
        assertThat(result.getTotalFare()).isEqualTo(200);
    }

    @Test
    void appliesSurgeMultiplierToTheWholeFare() {
        when(surgeCalculator.getSurgeMultiplier(true)).thenReturn(1.5);

        PricingResult result = fareCalculator.calculateFare(new DistanceResult(10), true);

        assertThat(result.getTotalFare()).isEqualTo(300);
        verify(surgeCalculator).getSurgeMultiplier(true);
    }

    @Test
    void roundsTotalFareToNearestWholeNumber() {
        when(surgeCalculator.getSurgeMultiplier(false)).thenReturn(1.0);

        // 50 + 2.37 km * 15 = 85.55 -> 86
        PricingResult result = fareCalculator.calculateFare(new DistanceResult(2.37), false);

        assertThat(result.getTotalFare()).isEqualTo(86);
    }
}
