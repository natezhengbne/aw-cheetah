package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserInfoDto {
    private String email;
    private String name;
    private String password;
}
