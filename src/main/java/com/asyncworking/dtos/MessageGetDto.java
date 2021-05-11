package com.asyncworking.dtos;

import com.asyncworking.models.Category;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data

public class MessageGetDto {
    private Long id;



    @NotNull(message = "message must have a title")
    private String messageTitle;

    private String content;

    private Category category;

    private String docURL;
}
