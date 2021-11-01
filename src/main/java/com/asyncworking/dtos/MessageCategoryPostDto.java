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
public class MessageCategoryPostDto {
    @NotNull(message = "projectId cannot be null.")
    private Long projectId;

    @NotNull(message = "categoryName cannot be null.")
    @Size(max = 128, message = "Category name can not be more than 128 characters.")
    private String categoryName;

    private String emoji;
}
