package com.asyncworking.dtos;

import com.asyncworking.models.Category;
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

    @NotNull(message = "companyId cannot be null")
    private Long companyId;

    @NotNull(message = "projectId cannot be null")
    private Long projectId;

    @NotNull(message = "message must have a title")
    private String messageTitle;

    private String content;

    private Category category;

    private String docURL;


}
