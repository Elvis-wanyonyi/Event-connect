package com.wolfcode.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventsTicket {

    private String description;
    private LocalDate date;
    private LocalTime time;
    private String location;
    private String organizer;

    private List<AllTickets> tickets;
}
