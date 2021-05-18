package com.asyncworking.dtos.todoitem;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoItemGetDto {

    private Long id;

    private String description;

    private String notes;
}
