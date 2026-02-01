package com.ryde.rideservice.service;

import com.ryde.rideservice.client.NotificationClient;
import com.ryde.rideservice.client.RealtimeClient;
import com.ryde.rideservice.dto.*;
import com.ryde.rideservice.model.Ride;
import com.ryde.rideservice.model.RideStatus;
import com.ryde.rideservice.repository.RideRepository;
import com.ryde.rideservice.service.orchestration.RideRequestOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository repository;
    private final RealtimeClient client;
    private final NotificationClient notificationClient;
    private final RideRequestOrchestrator orchestrator;

    public RideResponse createRide(Long riderId, RideRequest request) {
        Ride ride = Ride
                .builder()
                .riderId(riderId)
                .pickupLat(request.getPickupLat())
                .pickupLng(request.getPickupLng())
                .dropLat(request.getDropLat())
                .dropLng(request.getDropLng())
                .status(RideStatus.REQUESTED)
                .build();
        repository.save(ride);
        List<NearbyDriverResponse> drivers = client.getNearbyDrivers(request.getPickupLat(), request.getPickupLng());
        if (drivers == null || drivers.isEmpty()) {
            return new RideResponse(ride.getId(), ride.getStatus(), null);
        }
        RideQuote quote = orchestrator.buildRideQuote(request, drivers.size()<20);
        for (NearbyDriverResponse driver: drivers){
            notificationClient.notifyDriver(NotificationEvent
                    .builder()
                    .userId(driver.getDriverId())
                    .type(String.valueOf(RideStatus.REQUESTED))
                    .eta(quote.getEta())
                    .totalFare(quote.getPrice().getTotalFare())
                    .distanceInKm(quote.getDistance().getDistanceInKm())
                    .build());
        }
//        ride.setDriverId(assignedDriver);
//        ride.setStatus(RideStatus.DRIVER_ASSIGNED);
//        repository.save(ride);
//        return new RideResponse(
//                riderId,
//                ride.getStatus(),
//                assignedDriver
//        );
        return null;
    }
}