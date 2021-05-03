package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class ProjectDto {

    @NotEmpty(message = "Project name can not be empty")
    @Size(max = 128, message = "Project name can not be more than 128 characters! ")
    private String name;

    @NotNull(message = "OwnerId can not be empty")
    private Long ownerId;

    @NotNull(message = "companyId can not be empty")
    private Long companyId;

}
