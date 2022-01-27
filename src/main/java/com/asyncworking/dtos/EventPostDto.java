package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventPostDto {

    @NotNull(message = "Event title can not be null")
    @Size(max = 128, message = "Event title can not be more than than 128 characters.")
    private String title;

    @Size(max = 2048, message = "Event description can not be more than 2048 characters.")
    private String description;

    private boolean allDayEvent = false;

    private OffsetDateTime startTime;

    private OffsetDateTime endTime;
}
