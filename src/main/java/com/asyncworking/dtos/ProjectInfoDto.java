package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfoDto {

    private Long id;

    private Long leaderId;

    private String name;

    private String description;

    private List<String> projectUserNames;
}
