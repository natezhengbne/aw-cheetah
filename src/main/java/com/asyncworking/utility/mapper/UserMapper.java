package com.asyncworking.utility.mapper;

import com.asyncworking.constants.Status;
import com.asyncworking.dtos.AccountDto;
import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.dtos.InvitedAccountGetDto;
import com.asyncworking.dtos.InvitedAccountPostDto;
import com.asyncworking.dtos.todoitem.AssignedPeopleGetDto;
import com.asyncworking.models.Company;
import com.asyncworking.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

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
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public UserEntity mapInfoDtoToEntityInvitation(AccountDto accountDto) {
        String encodedPassword = passwordEncoder.encode(accountDto.getPassword());
        return UserEntity.builder()
                .name(accountDto.getName())
                .email(accountDto.getEmail().toLowerCase())
                .password(encodedPassword)
                .status(Status.ACTIVATED)
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public Company mapInfoDtoToEntity(CompanyModificationDto companyModificationDto) {
        return Company.builder()
                .id(companyModificationDto.getCompanyId())
                .name(companyModificationDto.getName())
                .description(companyModificationDto.getDescription())
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public AccountDto mapEntityToInfoDto(UserEntity userEntity) {
        return AccountDto.builder()
                .email(userEntity.getEmail())
                .name(userEntity.getName())
                .title(userEntity.getTitle())
                .build();
    }

    public CompanyModificationDto mapEntityToCompanyProfileDto(Company company) {
        return CompanyModificationDto.builder()
                .companyId(company.getId())
                .name(company.getName())
                .description(company.getDescription())
                .build();
    }

    public UserEntity mapInvitedDtoToEntityInvitation(InvitedAccountPostDto accountDto) {
        String encodedPassword = passwordEncoder.encode(accountDto.getPassword());

        return UserEntity.builder()
                .name(accountDto.getName())
                .email(accountDto.getEmail())
                .password(encodedPassword)
                .status(Status.ACTIVATED)
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .build();
    }

    public InvitedAccountGetDto mapEntityToInvitedDto(UserEntity returnedUser, String token) {
        return InvitedAccountGetDto.builder()
                .id(returnedUser.getId())
                .name(returnedUser.getName())
                .email(returnedUser.getEmail())
                .token(token)
                .build();
    }

    public AssignedPeopleGetDto mapEntityToAssignedPeopleDto(UserEntity userEntity) {
        return AssignedPeopleGetDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .build();
    }
}
