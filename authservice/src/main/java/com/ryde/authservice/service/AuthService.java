package com.ryde.authservice.service;

import com.ryde.authservice.dto.AuthResponse;
import com.ryde.authservice.dto.LoginRequest;
import com.ryde.authservice.dto.RefreshRequest;
import com.ryde.authservice.dto.RegisterRequest;
import com.ryde.authservice.model.Role;
import com.ryde.authservice.model.User;
import com.ryde.authservice.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisRefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtService jwtService, RedisRefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public User register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRoles(Set.of(Role.RIDER));
        return userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request){
        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = (User) auth.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.create(user.getId());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request){
        Long userId = refreshTokenService.verify(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String newAccessToken = jwtService.generateAccessToken(user);
        return new AuthResponse(newAccessToken, request.getRefreshToken());
    }

    public void logout(String refreshToken, User currentUser) {
        boolean deleted = refreshTokenService.delete(refreshToken);
        if (!deleted) {
            throw new IllegalStateException("Invalid refresh token");
        }
        currentUser.setTokenVersion(currentUser.getTokenVersion() + 1);
        userRepository.save(currentUser);
    }
}