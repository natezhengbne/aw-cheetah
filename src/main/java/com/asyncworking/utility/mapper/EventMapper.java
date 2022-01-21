package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.models.Event;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;

import static java.time.ZoneOffset.UTC;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedTime", expression = "java(getCurrentTime())")
    Event eventPostDtoToEvent(EventPostDto dto);

    EventGetDto eventToEventGetDto(Event event);

    default OffsetDateTime getCurrentTime() {
        return OffsetDateTime.now(UTC);
    }
}
