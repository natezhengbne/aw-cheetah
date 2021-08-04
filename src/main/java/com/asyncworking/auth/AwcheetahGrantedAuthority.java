package com.asyncworking.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class AwcheetahGrantedAuthority implements GrantedAuthority {
    private final String role;
    private final Long targetId;

    public AwcheetahGrantedAuthority(String role, Long targetId) {
        this.role = role;
        this.targetId = targetId;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }

    public Long getTargetId() {
        return this.targetId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AwcheetahGrantedAuthority) {
            return this.role.equals(((AwcheetahGrantedAuthority) obj).role)
                    && this.targetId.equals(((AwcheetahGrantedAuthority) obj).targetId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, role);
    }

    @Override
    public String toString() {
        return targetId + role;
    }
}
