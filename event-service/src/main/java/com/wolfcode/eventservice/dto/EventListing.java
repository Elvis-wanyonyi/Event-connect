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
public class EventListing {
    private String eventCode;
    private String eventName;
    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String organizer;
    private EventCategory eventCategory;
}
