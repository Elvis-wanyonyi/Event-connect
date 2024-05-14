package com.wolfcode.invitationservice.dto;

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
    private String body;
}
