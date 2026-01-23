package com.ryde.realtimeservice.service;

import com.ryde.realtimeservice.dto.DriverStatus;
import com.ryde.realtimeservice.dto.NearbyDriverResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RealtimeLocationService {

    private static final String DRIVER_GEO = "drivers:geo";
    private static final String DRIVER_ONLINE = "drivers:online";
    private static final String RIDER_GEO = "riders:geo";
    private final RedisTemplate<String, String> redis;

    public RealtimeLocationService(@Qualifier("redisTemplate") RedisTemplate<String, String> redis) {
        this.redis = redis;
    }

    public DriverStatus driverOnline(Long driverId) {
        redis.opsForSet().add(DRIVER_ONLINE, driverId.toString());
        return DriverStatus.ONLINE;
    }

    public DriverStatus driverOffline(Long driverId) {
        redis.opsForSet().remove(DRIVER_ONLINE, driverId.toString());
        redis.opsForGeo().remove(DRIVER_GEO, driverId.toString());
        return DriverStatus.OFFLINE;
    }

    public void updateDriverLocation(Long userId, double latitude, double longitude) {
        if (Boolean.FALSE.equals(redis.opsForSet().isMember(DRIVER_ONLINE, userId.toString()))) {
            throw new RuntimeException("Driver is offline");
        }
        redis.opsForGeo().add(
                DRIVER_GEO,
                new Point(longitude, latitude),
                userId.toString()
        );
    }

    public List<NearbyDriverResponse> findNearbyDrivers(double latitude, double longitude) {

        Circle circle = new Circle(new Point(longitude, latitude),
                new Distance(5, Metrics.KILOMETERS));

        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redis.opsForGeo()
                        .search(
                                DRIVER_GEO,
                                GeoReference.fromCircle(circle),
                                new Distance(5, Metrics.KILOMETERS),
                                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs()
                                        .includeDistance()
                                        .sortAscending()
                                        .limit(20)
                        );

        if (results == null) {
            return List.of();
        }

        return results.getContent().stream()
                .map(r -> new NearbyDriverResponse(
                        Long.parseLong(r.getContent().getName()),
                        r.getDistance().getValue()
                ))
                .toList();
    }

    public void updateRiderLocation(Long userId, double latitude, double longitude) {
        redis.opsForGeo().add(
                RIDER_GEO,
                new Point(longitude, latitude),
                userId.toString()
        );
    }
}