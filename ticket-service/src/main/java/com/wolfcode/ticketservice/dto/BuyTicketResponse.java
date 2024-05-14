package com.wolfcode.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyTicketResponse {

    private String ticketCode;
    private String serialNo;
    private String ticketType;
    private String amount;
}

