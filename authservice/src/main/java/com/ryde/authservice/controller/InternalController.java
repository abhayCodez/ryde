package com.ryde.authservice.controller;

import com.ryde.authservice.model.Role;
import com.ryde.authservice.model.User;
import com.ryde.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalController {

    @Value("${internal.key}")
    private String INTERNAL_KEY;
    private final UserRepository repo;

    @PatchMapping("/{userId}/upgrade-to-driver")
    public void upgradeToDriver(@PathVariable Long userId,
                                @RequestHeader("X-INTERNAL-KEY") String key) {
        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.getRoles().add(Role.DRIVER);
        user.setTokenVersion(user.getTokenVersion() + 1);
        repo.save(user);
    }
}