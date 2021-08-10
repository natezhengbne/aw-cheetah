package com.asyncworking.jwt;

public enum JwtClaims {

    AUTHORIZATION("Authorization"),

    AUTHORIZATION_TYPE("Bearer "),

    AUTHORITIES("authorities"),

    COMPANY_IDS("companyIds"),

    PROJECT_IDS("projectIds"),

    TARGET_ID("targetId"),

    ROLE("role");

    private final String value;

    JwtClaims(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
