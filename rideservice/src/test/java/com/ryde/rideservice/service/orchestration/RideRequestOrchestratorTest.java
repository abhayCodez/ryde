package com.ryde.rideservice.service.orchestration;

import com.ryde.rideservice.distance.DistanceCalculator;
import com.ryde.rideservice.distance.DistanceResult;
import com.ryde.rideservice.dto.RideQuote;
import com.ryde.rideservice.dto.RideRequest;
import com.ryde.rideservice.eta.EtaCalculator;
import com.ryde.rideservice.pricing.FareCalculator;
import com.ryde.rideservice.pricing.PricingResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideRequestOrchestratorTest {

    @Mock
    private DistanceCalculator distanceCalculator;

    @Mock
    private FareCalculator fareCalculator;

    @Mock
    private EtaCalculator etaCalculator;

    @InjectMocks
    private RideRequestOrchestrator orchestrator;

    @Test
    void buildsTheQuoteFromDistanceFareAndEta() {
        RideRequest request = new RideRequest(26.85, 80.949, 26.87, 80.99);
        DistanceResult distance = new DistanceResult(4.5);
        PricingResult price = new PricingResult(50, 67.5, 1.5, 176);
        when(distanceCalculator.calculate(26.85, 80.949, 26.87, 80.99)).thenReturn(distance);
        when(fareCalculator.calculateFare(distance, true)).thenReturn(price);
        when(etaCalculator.estimateMinutes(distance)).thenReturn(9);

        RideQuote quote = orchestrator.buildRideQuote(request, true);

        assertThat(quote.getDistance()).isSameAs(distance);
        assertThat(quote.getPrice()).isSameAs(price);
        assertThat(quote.getEta()).isEqualTo(9);
    }
}
