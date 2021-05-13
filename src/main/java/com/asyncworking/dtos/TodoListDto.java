package com.asyncworking.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListDto {

    private Long id;

    @NotNull(message = "projectId cannot be null")
    private Long projectId;

    @Size(max = 255, message = "Todo List Title can not be more than 255 characters! ")
    @NotNull(message = "todoList must have a title")
    private String todoListTitle;

    @Size(max = 2048, message = "Details can not be more than 2048 characters! ")
    private String details;

    private String docURL;

}
