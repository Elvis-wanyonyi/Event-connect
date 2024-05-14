package com.wolfcode.ticketservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TicketResponse(

        String ticketCode,
        String ticketType,
        Double price,
        int quantity,
        LocalDate deadline) {
}
