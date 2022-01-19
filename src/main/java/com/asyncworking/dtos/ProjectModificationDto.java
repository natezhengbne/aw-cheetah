package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectModificationDto {

    @NotNull(message = "Project id can not be null.")
    private Long projectId;

    @NotBlank(message = "Project name can not be blank.")
    @Size(max = 128, message = "Project name can not be more than 128 characters.")
    private String name;

    @Size(max = 1024, message = "Description cannot exceed 1024 characters.")
    private String description;

}
