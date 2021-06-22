package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.AvailableEmployeesGetDto;
import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.models.IAvailableEmployeeInfo;
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

    public AvailableEmployeesGetDto mapAvailableEmployeesEntityToDto(IAvailableEmployeeInfo availableEmployeeInfo) {
        return AvailableEmployeesGetDto.builder()
                .id(availableEmployeeInfo.getId())
                .name(availableEmployeeInfo.getName())
                .email(availableEmployeeInfo.getEmail())
                .title(availableEmployeeInfo.getTitle())
                .build();
    }
}
