package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInfoDto {

    private Long id;

    @Email(message = "Email should be a valid email.")
    private String email;

    private String name;

    private String password;

    @Size(max = 128, message = "Title can not be more than 128 characters.")
    private String title;

    private String accessToken;
}
