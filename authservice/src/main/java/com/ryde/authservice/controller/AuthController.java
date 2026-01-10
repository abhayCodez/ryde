package com.ryde.authservice.controller;

import com.ryde.authservice.dto.*;
import com.ryde.authservice.model.User;
import com.ryde.authservice.repository.UserRepository;
import com.ryde.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        User saved = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved.getId());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.login(request));
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestBody RefreshRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.refresh(request)).getBody();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // always safe

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        authService.logout(request.getRefreshToken(), user);
        return ResponseEntity.ok("Logout Successfull!!!");
    }
}