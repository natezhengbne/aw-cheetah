package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyModificationDto {

    private Long companyId;

    @NotBlank(message = "Company name can not be blank")
    @Size(max = 128, message = "Company name can not be more than 128 characters!")
    private String name;

    @Email(message = "Admin Email is not valid")
    private String adminEmail;

    @Size(max = 1024, message = "Description cannot exceed 1024 characters! ")
    private String description;

    @Size(max = 128, message = "Title can not be more than 128 characters! ")
    private String userTitle;

    private String website;

    private String contactNumber;

    private String contactEmail;

    private String industry;

}
