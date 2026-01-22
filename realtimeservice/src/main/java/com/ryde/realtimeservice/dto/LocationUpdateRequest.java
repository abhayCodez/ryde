package com.ryde.realtimeservice.dto;

import lombok.Data;

@Data
public class LocationUpdateRequest {
    private double latitude;
    private double longitude;
}