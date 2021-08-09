package com.asyncworking.models;

public enum RoleNames {

    COMPANY_MANAGER("Company Manager"),

    PROJECT_MANAGER("Project Manager");

    private final String value;

    RoleNames(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
