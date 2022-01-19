package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.EventGetDto;
import com.asyncworking.dtos.EventPostDto;
import com.asyncworking.models.Event;
import com.asyncworking.models.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "title", source = "dto.title")
    Event eventPostDtoToEvent(EventPostDto dto, UserEntity user);

    EventGetDto eventToEventGetDto(Event event);
}
