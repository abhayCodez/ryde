package com.ryde.notificationservice.filter;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final String role;

    public JwtAuthenticationToken(Long userId, String role) {
        super(List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        this.userId = userId;
        this.role = role;
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