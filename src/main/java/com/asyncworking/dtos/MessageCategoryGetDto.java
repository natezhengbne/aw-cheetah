package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageCategoryGetDto {
    private Long messageCategoryId;

    private Long projectId;

    private String categoryName;

    private String emoji;
}
