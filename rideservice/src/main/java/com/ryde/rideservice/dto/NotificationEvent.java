package com.ryde.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationEvent {
    private Long userId;
    private String type;
    private double totalFare;
    private double distanceInKm;
    private int eta;
}