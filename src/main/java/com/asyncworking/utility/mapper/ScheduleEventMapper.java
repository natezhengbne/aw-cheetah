package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.ScheduleEventGetDto;
import com.asyncworking.dtos.ScheduleEventPostDto;
import com.asyncworking.models.ScheduleEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {OffsetDateTime.class, ZoneOffset.class})
public interface ScheduleEventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", expression = "java(OffsetDateTime.now(ZoneOffset.UTC))")
    @Mapping(target = "updatedTime", expression = "java(OffsetDateTime.now(ZoneOffset.UTC))")
    ScheduleEvent eventPostDtoToEvent(Long companyId, Long projectId, Long ownerId, ScheduleEventPostDto dto);

    ScheduleEventGetDto eventToEventGetDto(ScheduleEvent scheduleEvent);
}
