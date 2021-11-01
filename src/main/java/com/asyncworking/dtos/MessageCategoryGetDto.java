package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCategoryGetDto {
    private Long messageCategoryId;

    private Long projectId;

    @Size(max = 128, message = "Category name can not be more than 128 characters.")
    private String categoryName;

    private String emoji;
}
