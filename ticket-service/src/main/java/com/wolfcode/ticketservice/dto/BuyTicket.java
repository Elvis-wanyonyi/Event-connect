package com.wolfcode.ticketservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyTicket {

    @Email
    private String email;
    private String countryCode;
    @Pattern(regexp = "^\\d{9}$", message = "Enter a valid phone number")
    private String phone;
}
