package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
