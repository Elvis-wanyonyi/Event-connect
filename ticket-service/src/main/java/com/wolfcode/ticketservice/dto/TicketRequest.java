package com.wolfcode.ticketservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {

    @NotBlank(message = "enter ticket type/class ")
    private String ticketType;
    private String price;
    private int quantity;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate deadline;
}
