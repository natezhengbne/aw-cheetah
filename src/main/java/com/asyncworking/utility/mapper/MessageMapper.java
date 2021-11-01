package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.MessageCategoryGetDto;
import com.asyncworking.dtos.MessageCategoryPostDto;
import com.asyncworking.dtos.MessageGetDto;
import com.asyncworking.dtos.MessagePostDto;
import com.asyncworking.models.Message;
import com.asyncworking.models.MessageCategory;
import com.asyncworking.models.Project;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Component
public class MessageMapper {
    public MessageGetDto fromEntity(Message message, String userName) {
        MessageCategory messageCategory = message.getMessageCategory();
        return MessageGetDto.builder()
                .id(message.getId())
                .posterUserId(message.getPosterUserId())
                .posterUser(userName)
                .messageTitle(message.getMessageTitle())
                .content(message.getContent())
                .messageCategoryName((messageCategory == null) ? null : messageCategory.getCategoryName())
                .messageCategoryEmoji((messageCategory == null) ? null : messageCategory.getEmoji())
                .originNotes(message.getOriginNotes())
                .docURL(message.getDocURL())
                .postTime(message.getPostTime())
                .messageCategoryId((messageCategory == null) ? null : messageCategory.getId())
                .subscribersIds(message.getSubscribersIds())
                .build();
    }

    public Message toEntity(MessagePostDto messagePostDto, Project project, MessageCategory messageCategory) {
        return Message.builder()
                .project(project)
                .companyId(messagePostDto.getCompanyId())
                .posterUserId(messagePostDto.getPosterUserId())
                .messageCategory(messageCategory)
                .messageTitle(messagePostDto.getMessageTitle())
                .docURL(messagePostDto.getDocURL())
                .content(messagePostDto.getContent())
                .originNotes(messagePostDto.getOriginNotes())
                .createdTime(OffsetDateTime.now(UTC))
                .updatedTime(OffsetDateTime.now(UTC))
                .postTime(OffsetDateTime.now(UTC))
                .subscribersIds(messagePostDto.getSubscribersIds())
                .build();
    }


    public MessageCategoryGetDto fromCategoryEntity(MessageCategory messageCategory) {
        return MessageCategoryGetDto.builder()
                .messageCategoryId(messageCategory.getId())
                .projectId(messageCategory.getProject().getId())
                .categoryName(messageCategory.getCategoryName())
                .emoji(messageCategory.getEmoji())
                .build();
    }

    public MessageCategory toCategoryEntity(MessageCategoryPostDto messageCategoryPostDto, Project project) {
        if (messageCategoryPostDto.getEmoji() == null) {
            return MessageCategory.builder()
                    .project(project)
                    .categoryName(messageCategoryPostDto.getCategoryName())
                    .emoji("")
                    .build();
        }
        return MessageCategory.builder()
                .project(project)
                .categoryName(messageCategoryPostDto.getCategoryName())
                .emoji(messageCategoryPostDto.getEmoji())
                .build();
    }

    public MessageCategory toCategoryEntity(Project project, String categoryName, String emoji) {
        return MessageCategory.builder()
                .project(project)
                .categoryName(categoryName)
                .emoji(emoji)
                .build();
    }
}
