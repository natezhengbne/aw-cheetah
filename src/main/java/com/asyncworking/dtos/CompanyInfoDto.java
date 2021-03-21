package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CompanyInfoDto {

    private String name;

    private String adminEmail;

    private String description;

    private String userTitle;

    private String website;

    private String contactNumber;

    private String contactEmail;

    private String industry;

}
