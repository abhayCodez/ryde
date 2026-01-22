package com.ryde.profileservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class DriverProfileResponse {
    private Long userId;
    private String license;
    List<VehicleResponse> vehicles;
}