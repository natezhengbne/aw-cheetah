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
public class ProjectDto {

    @NotBlank(message = "Project name can not be blank.")
    @Size(max = 128, message = "Project name can not be more than 128 characters.")
    private String name;

    @NotNull(message = "ownerId can not be null.")
    private Long ownerId;

    @NotNull(message = "companyId can not be null.")
    private Long companyId;

    private String description;

    private boolean ifPrivate;

    private String defaultView;

}
