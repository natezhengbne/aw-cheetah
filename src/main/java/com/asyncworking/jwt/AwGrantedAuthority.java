package com.asyncworking.jwt;

import org.springframework.security.core.GrantedAuthority;

public class AwGrantedAuthority implements GrantedAuthority {
    private final String authority;
    private final Long id;

    public AwGrantedAuthority(String authority, Long id) {
        this.authority = authority;
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }


}
