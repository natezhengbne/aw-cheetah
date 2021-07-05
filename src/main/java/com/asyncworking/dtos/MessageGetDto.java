package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageGetDto {
    private Long id;

    private String messageTitle;

    private Long posterUserId;

    private String posterUser;

    private String content;

    private Long messageCategoryId;

    private String messageCategoryName;

    private String messageCategoryEmoji;

    private String docURL;

    private OffsetDateTime postTime;

    private String originNotes;
}
