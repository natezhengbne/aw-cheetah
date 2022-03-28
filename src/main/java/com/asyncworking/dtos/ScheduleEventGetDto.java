package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleEventGetDto {

    private Long id;

    private Long companyId;

    private Long projectId;

    private Long ownerId;

    private String title;

    private String description;

    private boolean allDayEvent;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private OffsetDateTime createdTime;

    private OffsetDateTime updatedTime;
}
