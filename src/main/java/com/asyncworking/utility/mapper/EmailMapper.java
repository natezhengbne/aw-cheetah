package com.asyncworking.utility.mapper;

import com.asyncworking.constants.EmailType;
import com.asyncworking.dtos.CompanyInvitedAccountDto;
import com.asyncworking.dtos.EmailContentDto;
import com.asyncworking.dtos.EmailMessageDto;
import com.asyncworking.models.EmailSendRecord;
import com.asyncworking.models.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmailMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "receiverId")
    @Mapping(target = "receiver", source = "receiverEmail")
    @Mapping(target = "sendStatus", constant = "false")
    @Mapping(target = "sendTime", expression = "java(getCurrentTime())")
    EmailSendRecord toEmailSendRecord(Long receiverId, EmailType emailType, String receiverEmail);

    EmailMessageDto toEmailMessageDto(
            EmailContentDto emailContentDto,
            Long emailRecordId,
            String templateS3Bucket,
            String templateS3Key);

    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "verificationLink", source = "link")
    @Mapping(target = "templateType", constant = "emailTypeString")
    EmailContentDto toEmailContentDto(String emailTypeString, String link, UserEntity user);

    @Mapping(target = "email", source = "accountDto.email")
    @Mapping(target = "userName", source = "accountDto.name")
    @Mapping(target = "verificationLink", source = "link")
    @Mapping(target = "templateType", constant = "emailTypeString")
    EmailContentDto toEmailContentDto(
            String emailTypeString,
            String link,
            CompanyInvitedAccountDto accountDto,
            String companyName,
            String companyOwnerName);

    default OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now(UTC);
    }

}
