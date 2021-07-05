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
public class MessageCategoryPostDto {
    @NotNull(message = "projectId cannot be null.")
    private Long projectId;

    @NotNull(message = "categoryName cannot be null.")
    private String categoryName;

    private String emoji;
}
