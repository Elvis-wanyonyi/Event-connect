package com.wolfcode.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventHistory {

    private String eventName;
    private String description;
    private String ticketType;
    private double price;
    private LocalDate date;
    private LocalTime time;
    private String location;

    private EventResponse eventResponse;

}
