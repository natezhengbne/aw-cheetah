package com.asyncworking.utility.mapper;

import com.asyncworking.constants.EmailType;
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
public interface EmailSendRecordMapper {

    @Mapping(target = "userEntity", source = "userEntity")
    @Mapping(target = "receiver", source = "receiverEmail")
    @Mapping(target = "sendStatus", constant = "false")
    @Mapping(target = "sendTime", expression = "java(getCurrentTime())")
    EmailSendRecord toEmailSendRecord(UserEntity userEntity, EmailType emailType, String receiverEmail);

    default OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now(UTC);
    }
}
