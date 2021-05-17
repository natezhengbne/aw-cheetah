package com.asyncworking.dtos.todoitem;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemGetDto {

    private String notes;

    private String description;
}
