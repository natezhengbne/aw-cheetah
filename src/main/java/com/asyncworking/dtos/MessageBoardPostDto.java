package com.asyncworking.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MessageBoardPostDto {
    @NotNull(message = "projectId cannot be null")
    private Long projectId;
}
