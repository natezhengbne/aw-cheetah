package com.asyncworking.jwt;

import org.springframework.security.core.GrantedAuthority;

public class AwGrantedAuthority implements GrantedAuthority {
    private final String role;
    private final Long targetId;

    public AwGrantedAuthority(String role, Long targetId) {
        this.role = role;
        this.targetId = targetId;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public Long getTargetId() { return this.targetId; }

}
