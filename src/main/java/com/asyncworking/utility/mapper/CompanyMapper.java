package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.CompanyInfoGetDto;
import com.asyncworking.models.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    public CompanyInfoGetDto mapEntityToDto(Company company) {
       return CompanyInfoGetDto.builder()
                .id(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }
}
