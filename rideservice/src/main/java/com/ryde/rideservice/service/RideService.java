package com.ryde.rideservice.service;

import com.ryde.rideservice.client.NotificationClient;
import com.ryde.rideservice.client.RealtimeClient;
import com.ryde.rideservice.dto.NearbyDriverResponse;
import com.ryde.rideservice.dto.NotificationEvent;
import com.ryde.rideservice.dto.RideRequest;
import com.ryde.rideservice.dto.RideResponse;
import com.ryde.rideservice.model.Ride;
import com.ryde.rideservice.model.RideStatus;
import com.ryde.rideservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository repository;
    private final RealtimeClient client;
    private final NotificationClient notificationClient;

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
        for (NearbyDriverResponse driver: drivers){
            notificationClient.notifyDriver(NotificationEvent
                    .builder()
                    .userId(driver.getDriverId())
                    .type(String.valueOf(RideStatus.REQUESTED))
                    .payload(ride)
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