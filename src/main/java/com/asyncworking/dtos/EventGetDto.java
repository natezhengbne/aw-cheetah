package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventGetDto {

    private Long id;

    private String title;

    private String description;

    private boolean isAllDay;

    private OffsetDateTime startDate;

    private OffsetDateTime endDate;
}
