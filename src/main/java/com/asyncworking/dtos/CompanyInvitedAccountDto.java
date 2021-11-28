package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInvitedAccountDto {
    @Size(max = 128, message = "Name can not be more than 128 characters.")
    private String name;

    @NotBlank(message = "Email must not be blank.")
    @Size(max = 128, message = "Email name can not be more than 128 characters.")
    @Email(message = "Email should be a valid email.")
    private String email;

    @Size(max = 128, message = "Title can not be more than 128 characters.")
    private String title;
}
