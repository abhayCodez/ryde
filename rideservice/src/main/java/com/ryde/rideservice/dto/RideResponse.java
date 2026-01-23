package com.ryde.rideservice.dto;

import com.ryde.rideservice.model.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RideResponse {
    private Long userId;
    private RideStatus status;
    private Long driverId;
}