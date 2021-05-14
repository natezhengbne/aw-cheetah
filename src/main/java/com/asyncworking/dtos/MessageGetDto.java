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
public class MessageGetDto {
    private Long id;

    @NotNull(message = "message must have a title")
    private String messageTitle;

    private String content;

    private Category category;

    private String docURL;
}
