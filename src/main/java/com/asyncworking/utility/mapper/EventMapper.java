package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.models.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {OffsetDateTime.class, ZoneOffset.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", expression = "java(OffsetDateTime.now(ZoneOffset.UTC))")
    @Mapping(target = "updatedTime", expression = "java(OffsetDateTime.now(ZoneOffset.UTC))")
    Event eventPostDtoToEvent(Long companyId, Long projectId, Long ownerId, EventPostDto dto);

    EventGetDto eventToEventGetDto(Event event);
}
