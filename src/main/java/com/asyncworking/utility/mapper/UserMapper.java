package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.CompanyInfoPostDto;
import com.asyncworking.models.Company;
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

    public Company mapInfoDtoToEntity(CompanyInfoPostDto companyModificationDto) {
        return Company.builder()
                .id(companyModificationDto.getCompanyId())
                .name(companyModificationDto.getName())
                .description(companyModificationDto.getDescription())
                .build();
    }

    public AccountDto mapEntityToInfoDto(UserEntity userEntity) {
        return AccountDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .title(userEntity.getTitle())
                .build();
    }

    public CompanyInfoPostDto mapEntityToCompanyProfileDto(Company company) {
        return CompanyInfoPostDto.builder()
                .companyId(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }
}
