package com.ryde.authservice.controller;

import com.ryde.authservice.dto.TokenResponse;
import com.ryde.authservice.model.Role;
import com.ryde.authservice.model.User;
import com.ryde.authservice.repository.UserRepository;
import com.ryde.authservice.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalController {

    @Value("${internal.key}")
    private String INTERNAL_KEY;
    private final UserRepository repo;
    private final JwtService jwtService;

    @PatchMapping(value = "/{userId}/upgrade-to-driver", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> upgradeToDriver(@PathVariable Long userId,
                                                         @RequestHeader("X-INTERNAL-KEY") String key) {
        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        User user = repo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.getRoles().add(Role.DRIVER);
        user.getRoles().remove(Role.RIDER);
        user.setTokenVersion(user.getTokenVersion() + 1);
        repo.save(user);
        String newAccessToken = jwtService.generateAccessToken(user);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new TokenResponse(newAccessToken));
    }
}