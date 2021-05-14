package com.asyncworking.utility.mapper;


import com.asyncworking.dtos.TodoListDto;
import com.asyncworking.models.Project;
import com.asyncworking.models.TodoList;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;


@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TodoMapper {

    TodoListDto fromEntity(TodoList todoList);

}
