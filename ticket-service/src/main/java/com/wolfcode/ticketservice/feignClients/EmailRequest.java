package com.wolfcode.ticketservice.feignClients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String from;
    private String to;
    private String subject;
    private String text;
}
