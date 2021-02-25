package com.asyncworking.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDto {
    private String email;
    private String name;
    private String password;
}
