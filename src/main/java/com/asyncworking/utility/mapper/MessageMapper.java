package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.models.Message;
import com.asyncworking.models.Project;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Component
public class MessageMapper {
    public MessageGetDto fromEntity (Message message, String userName) {
        return MessageGetDto.builder()
                .id(message.getId())
                .posterUserId(message.getPosterUserId())
                .posterUser(userName)
                .messageTitle(message.getMessageTitle())
                .content(message.getContent())
                .originNotes(message.getOriginNotes())
                .docURL(message.getDocURL())
                .postTime(message.getPostTime())
                .category(message.getCategory())
                .build();
    }

    public Message toEntity (MessagePostDto messagePostDto, Project project) {
        return Message.builder()
                .project(project)
                .companyId(messagePostDto.getCompanyId())
                .posterUserId(messagePostDto.getPosterUserId())
                .category(messagePostDto.getCategory())
                .messageTitle(messagePostDto.getMessageTitle())
                .docURL(messagePostDto.getDocURL())
                .content(messagePostDto.getContent())
                .originNotes(messagePostDto.getOriginNotes())
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .postTime(OffsetDateTime.now(UTC))
                .build();
    }

}
