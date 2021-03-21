package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.models.Company;
import com.asyncworking.dtos.UserInfoPostDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserEntity mapInfoDtoToEntity(AccountDto accountDto) {
        String encodedPassword = passwordEncoder.encode(accountDto.getPassword());
        return UserEntity.builder()
                .name(accountDto.getName())
                .email(accountDto.getEmail().toLowerCase())
                .password(encodedPassword)
                .status(Status.UNVERIFIED)
                .build();
    }

    public Company mapInfoDtoToEntity(CompanyModificationDto companyModificationDto) {
        return Company.builder()
                .id(companyModificationDto.getCompanyId())
                .name(companyModificationDto.getName())
                .description(companyModificationDto.getDescription())
                .build();
    }

    public UserInfoPostDto mapEntityToInfoDto(UserEntity userEntity) {
        return UserInfoPostDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .build();
    }

    public CompanyModificationDto mapEntityToCompanyProfileDto(Company company) {
        return CompanyModificationDto.builder()
                .companyId(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }
}
