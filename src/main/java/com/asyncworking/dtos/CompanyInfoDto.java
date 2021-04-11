package com.asyncworking.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyInfoDto {

    private Long id;

    private String name;

    private String adminEmail;

    private String description;

    private String userTitle;

    private String website;

    private String contactNumber;

    private String contactEmail;

    private String industry;

}
