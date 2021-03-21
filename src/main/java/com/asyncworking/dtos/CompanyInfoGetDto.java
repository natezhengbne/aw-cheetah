package com.asyncworking.dtos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyInfoGetDto {
    private Long id;

    private String name;

    private String description;
}
