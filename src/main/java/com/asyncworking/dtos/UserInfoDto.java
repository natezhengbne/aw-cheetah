package com.asyncworking.dtos;

import lombok.Data;

@Data
public class UserInfoDto {
    private long id;
    private String email;
    private String name;
    private String title;
    private String password;
}
