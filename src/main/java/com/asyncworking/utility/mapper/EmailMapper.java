package com.asyncworking.utility.mapper;

import com.asyncworking.constants.EmailType;
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

    @Mapping(target = "userEntity", source = "userEntity")
    @Mapping(target = "receiver", source = "receiverEmail")
    @Mapping(target = "sendStatus", constant = "false")
    @Mapping(target = "sendTime", expression = "java(getCurrentTime())")
    EmailSendRecord toEmailSendRecord(UserEntity userEntity, EmailType emailType, String receiverEmail);


    @Mapping(target = "templateType", expression = "java(templateType.toString())")
    @Mapping(target = "userName", expression = "java(userEntity.getName())")
    @Mapping(target = "email", source = "receiverEmail")
//    @Mapping(target = "templateS3Bucket", constant = "cloud.aws.S3.templateS3Bucket")
//    @Mapping(target = "templateS3Key", constant = "cloud.aws.S3.templateS3Key")
    @Mapping(target = "templateS3Bucket", constant = "aw-email-template-jh")
    @Mapping(target = "templateS3Key", constant = "verification_email_template.txt")

    EmailMessageDto toEmailMessageDto(UserEntity userEntity, String verificationLink, EmailType templateType, String receiverEmail);

    default OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now(UTC);
    }
}
