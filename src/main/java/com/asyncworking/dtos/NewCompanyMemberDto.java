package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCompanyMemberDto {

    private Long userId;

    private Long companyId;

    @Size(max = 128, message = "Title can not be more than 128 characters.")
    private String title;

    private OffsetDateTime createdTime;

    private OffsetDateTime updatedTime;
}
