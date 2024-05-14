package com.wolfcode.ticketservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolfcode.ticketservice.dto.BuyTicket;
import com.wolfcode.ticketservice.dto.BuyTicketResponse;
import com.wolfcode.ticketservice.dto.EventHistory;
import com.wolfcode.ticketservice.dto.TicketTypeRevenue;
import com.wolfcode.ticketservice.exception.TicketServiceException;
import com.wolfcode.ticketservice.service.SaleTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets/sale")
@Tag(name = "Ticket sales",
description = "Handles ticket sales for specific event tickets")
public class SaleTicketController {

    private final SaleTicketService saleTicketService;


    @PostMapping("/{ticketCode}/buy-now")
    @ResponseStatus(HttpStatus.OK)
    public BuyTicketResponse buyTicketNow(@PathVariable String ticketCode, @Valid @RequestBody BuyTicket buyTicket)
            throws TicketServiceException, JsonProcessingException {
        return saleTicketService.buyTicketNow(ticketCode, buyTicket);
    }

    @GetMapping("/validate-ticket/{serialNo}")
    @ResponseStatus(HttpStatus.OK)
    public String validateTicket(@PathVariable String serialNo) throws TicketServiceException {
        saleTicketService.validateTicket(serialNo);
        return "Ticket is valid";
    }

    @GetMapping("/check-in/{serialNo}")
    @Operation(operationId = "event check in")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkIn(@PathVariable String serialNo) {
        boolean checkedIn = saleTicketService.checkIn(serialNo);
        if (checkedIn) {
            return ResponseEntity.ok("Check-in successful");
        } else {
            return ResponseEntity.badRequest().body("Ticket already used or invalid");
        }
    }

    @GetMapping("/revoke/{ticketCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> revokeTicket(@PathVariable String ticketCode,
                                               @RequestParam String serialNo) throws TicketServiceException {
        boolean revoked = saleTicketService.revokeTicket(ticketCode, serialNo);
        if (revoked) {
            return ResponseEntity.ok("Ticket cancelled we've began processing your refund");
        } else {
            return ResponseEntity.badRequest().body("Ticket deadline has already passed");
        }

    }

    @GetMapping("/revenue/{ticketCode}")
    @ResponseStatus(HttpStatus.OK)
    public TicketTypeRevenue getRevenueForTicketType(@PathVariable String ticketCode, @RequestHeader("userRoles") String userRolesHeader) {
        List<String> userRoles = Arrays.asList(userRolesHeader.split(","));
        if (userRoles.contains("ADMIN")) {
            return saleTicketService.getRevenueForTicketType(ticketCode);
        } else {
            throw new RuntimeException("Access Denied");
        }
    }

    @GetMapping("/event-history/{email}")
    @ResponseStatus(HttpStatus.OK)
    public List<EventHistory> getEventHistory(@PathVariable String email) {
        return saleTicketService.getEventHistory(email);
    }


}
