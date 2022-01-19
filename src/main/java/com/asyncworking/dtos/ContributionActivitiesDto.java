package com.asyncworking.dtos;

import lombok.*;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContributionActivitiesDto {
    private String taskName;
    private OffsetDateTime dueDate;
}
