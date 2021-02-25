package com.asyncworking.dtos;

import lombok.*;

//@NoArgsConstructor
//@AllArgsConstructor
@Data
@Builder
public class UserInfoDto {
    private String email;
    private String name;
    private String password;
}
