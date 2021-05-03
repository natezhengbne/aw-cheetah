package com.asyncworking.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListDto {

    private Long id;

    @NotNull(message = "TodoBoardId cannot be null")
    private Long todoBoardId;

    @NotNull(message = "companyId cannot be null")
    private Long companyId;

    @NotNull(message = "projectId cannot be null")
    private Long projectId;

    @NotNull(message = "todoList must have a title")
    private String todoListTitle;

    private String details;

    private String docURL;

}
