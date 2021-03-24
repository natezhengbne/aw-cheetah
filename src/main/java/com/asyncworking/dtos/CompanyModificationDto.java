package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CompanyModificationDto {

    private Long companyId;

    @NotEmpty(message = "Company name can not be empty")
    @Size(max = 128, message = "Company name can not be more than 128 characters! ")
    private String name;

    @Email(message = "Email is not valid")
    private String adminEmail;

    @Size(max = 1024, message = "Description cannot exceed 1024 characters! ")
    private String description;

    @Size(max = 128, message = "Title can not be more than 128 characters! ")
    private String userTitle;

}
