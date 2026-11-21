package com.ryde.notificationservice.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import reactor.util.annotation.Nullable;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;

    public JwtAuthenticationToken(Long userId, List<String> roles) {
        super(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList());
        this.userId = userId;
        setAuthenticated(true);
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public @Nullable Object getCredentials() {
        return null;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return null;
    }
}