package com.wolfcode.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyEvents {

    private String eventName;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private EventCategory eventCategory;
}
