package com.ryde.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private Long userId;
    private String type;
    private double totalFare;
    private double distanceInKm;
    private int eta;
}