package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContributionActivitiesDto {
    private String taskName;
    private OffsetDateTime dueDate;
}
