package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModificationDto {

    @NotNull(message = "companyId can not be empty")
    private Long projectId;

    @NotEmpty(message = "Company name can not be empty")
    @Size(max = 128, message = "Company name can not be more than 128 characters! ")
    private String name;

    @Size(max = 1024, message = "Description cannot exceed 1024 characters! ")
    private String description;

}
