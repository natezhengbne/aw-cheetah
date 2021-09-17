package com.asyncworking.utility.mapper;

import com.asyncworking.dtos.AvailableEmployeesGetDto;
import com.asyncworking.dtos.EmployeeGetDto;
import com.asyncworking.models.IAvailableEmployeeInfo;
import com.asyncworking.models.IEmployeeInfo;
import com.asyncworking.models.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeGetDto mapEntityToDto(IEmployeeInfo employeeInfo) {
        return EmployeeGetDto.builder()
                .id(employeeInfo.getId())
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

    public EmployeeGetDto mapEntityToDto(UserEntity userEntity) {
        return EmployeeGetDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .title(userEntity.getTitle())
                .build();
    }
}
