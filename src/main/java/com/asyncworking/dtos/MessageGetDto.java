package com.asyncworking.dtos;

import com.asyncworking.models.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageGetDto {
    private Long id;

    private Long companyId;

    private String messageTitle;

    private Long projectId;

    private String content;

    private Category category;

    private String docURL;
}
