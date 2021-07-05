package com.asyncworking.dtos;

import com.asyncworking.models.MessageCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MessagePostDto {

    @NotNull(message = "companyId cannot be null.")
    private Long companyId;

    @NotNull(message = "projectId cannot be null.")
    private Long projectId;

    @NotNull(message = "Message must have a title.")
    private String messageTitle;

    @NotNull(message = "posterUserId cannot be null.")
    private Long posterUserId;

    private String content;

    private Long messageCategoryId;

    private String docURL;

    private String originNotes;
}
