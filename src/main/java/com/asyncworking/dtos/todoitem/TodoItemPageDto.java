package com.asyncworking.dtos.todoitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemPageDto {

    private Long todoListId;

    private String todoListTitle;

    private Long projectId;

    private String projectName;

    private TodoItemGetDto todoItemGetDto;

    private String createdUserName;

    private Map<Long, String> assignedPeople;
}
