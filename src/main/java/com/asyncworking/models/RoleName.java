package com.asyncworking.models;

public enum RoleName {

    COMPANY_MANAGER("Company Manager"),

    PROJECT_MANAGER("Project Manager");

    private final String value;

    RoleName(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
