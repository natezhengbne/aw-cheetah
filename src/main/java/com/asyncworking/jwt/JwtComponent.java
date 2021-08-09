package com.asyncworking.jwt;

public enum JwtComponent {

    AUTHORIZATION("Authorization"),

    AUTHORIZATION_TYPE("Bearer "),

    AUTHORITIES("authorities"),

    COMPANY_IDS("companyIds"),

    PROJECT_IDS("projectIds"),

    TARGET_ID("targetId"),

    Role("role");

    private final String value;

    JwtComponent(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
