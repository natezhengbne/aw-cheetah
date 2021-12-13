package com.asyncworking.dtos;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventsDto {
    private String id;
    private String name;
    private String where;
    private String when;
    private String description;

}
