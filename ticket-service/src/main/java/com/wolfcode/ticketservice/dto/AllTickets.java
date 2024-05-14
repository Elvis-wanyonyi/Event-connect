package com.wolfcode.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllTickets {

    private String ticketCode;
    private String eventCode;
    private String price;
    private int quantity;
    private String ticketType;
    private LocalDate deadline;
}
