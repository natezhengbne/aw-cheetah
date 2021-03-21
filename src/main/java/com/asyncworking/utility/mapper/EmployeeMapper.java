package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.models.IEmployeeInfo;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeGetDto mapEntityToDto(IEmployeeInfo employeeInfo) {
        return EmployeeGetDto.builder()
                .name(employeeInfo.getName())
                .email(employeeInfo.getEmail())
                .title(employeeInfo.getTitle())
                .build();
    }
}
