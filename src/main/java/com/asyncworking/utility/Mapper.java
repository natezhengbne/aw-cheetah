package com.asyncworking.utility;

import com.asyncworking.dtos.CompanyInfoDto;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Company;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RequiredArgsConstructor
@Component

public class Mapper {

    private final PasswordEncoder passwordEncoder;

    public UserEntity mapInfoDtoToEntity(UserInfoDto userInfoDto) {
        String encodedPassword = passwordEncoder.encode(userInfoDto.getPassword());
        return UserEntity.builder()
                .name(userInfoDto.getName())
                .email(userInfoDto.getEmail().toLowerCase())
                .password(encodedPassword)
                .status(Status.UNVERIFIED)
                .build();
    }

    public Company mapInfoDtoToEntity(CompanyInfoDto companyInfoDto){
        return Company.builder()
                .id(companyInfoDto.getCompanyId())
                .name(companyInfoDto.getName())
                .description(companyInfoDto.getDescription())
                .build();
    }

    public UserInfoDto mapEntityToInfoDto(UserEntity userEntity) {
        return UserInfoDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();
    }

    public CompanyInfoDto mapEntityToCompanyProfileDto(Company company){
        return CompanyInfoDto.builder()
                .companyId(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }
}
