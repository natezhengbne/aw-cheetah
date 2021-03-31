package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoPostDto {
    @NotEmpty(message = "Company name can not be empty")
    @Size(max = 128, message = "Company name can not be more than 128 characters ")
    private String name;

    @Email(message = "admin email should be a valid email")
    @NotEmpty(message = "admin email must not be empty")
    private String adminEmail;

    private String description;

    private String userTitle;

    private String website;

    private String contactNumber;

    private String contactEmail;

    private String industry;

}
