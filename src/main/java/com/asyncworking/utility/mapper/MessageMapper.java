package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.models.Message;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {
    Message toEntity(MessagePostDto messagePostDto);

    MessageGetDto fromEntity(Message message);

}
