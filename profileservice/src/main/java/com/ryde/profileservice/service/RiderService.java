package com.ryde.profileservice.service;

import com.ryde.profileservice.model.Rider;
import com.ryde.profileservice.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RiderService {

    private final RiderRepository repository;

    public Rider getRider(Long userId){
        Optional<Rider> rider = repository.findByUserId(userId);
        if (rider.isEmpty()){
            Rider r = new Rider();
            r.setUserId(userId);
            repository.save(r);
            return r;
        }
        return rider.get();
    }
}