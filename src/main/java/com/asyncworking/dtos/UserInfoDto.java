package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInfoDto {
    @Email(message = "email should be a valid email")
    @NotEmpty(message = "email must not be empty")
    private String email;

    private String name;

    @NotEmpty(message = "password must not be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=\\S+$).{8,}$",
            message = "Your password must be at least 8 character long and contains at least one non-letter character")
    private String password;

    private String title;
}
