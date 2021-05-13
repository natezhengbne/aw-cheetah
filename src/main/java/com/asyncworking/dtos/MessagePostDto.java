package com.asyncworking.dtos;

import com.asyncworking.models.Category;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data

public class MessagePostDto {
    private Long id;

    @NotNull(message = "companyId cannot be null")
    private Long companyId;

    @NotNull(message = "projectId cannot be null")
    private Long projectId;

    @NotNull(message = "project user Id cannot be null")
    private Long projectUserId;

    @NotNull(message = "message must have a title")
    private String messageTitle;

    private String content;

    private Category category;

    private String docURL;


}
