package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.models.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    public CompanyInfoDto mapEntityToDto(Company company) {
        return CompanyInfoDto.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }
}
