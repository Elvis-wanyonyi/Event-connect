package com.wolfcode.ticketservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolfcode.ticketservice.dto.AllTickets;
import com.wolfcode.ticketservice.dto.TicketRequest;
import com.wolfcode.ticketservice.dto.TicketResponse;
import com.wolfcode.ticketservice.exception.TicketServiceException;
import com.wolfcode.ticketservice.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tickets/")
@Tag(name = "Tickets", description = "Event ticket operations")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/{eventCode}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse createEventTicket(@RequestBody TicketRequest ticketRequest, @RequestHeader("userRoles") String userRolesHeader,
                                            @PathVariable String eventCode) throws JsonProcessingException, TicketServiceException {
        List<String> userRoles = Arrays.asList(userRolesHeader.split(","));
        if (userRoles.contains("ADMIN")) {
            return ticketService.createEventTicket(ticketRequest, eventCode);
        } else {
            throw new TicketServiceException("Access Denied");
        }
    }

    @GetMapping
    @Operation(summary = "Available tickets for all the available events")
    @ResponseStatus(HttpStatus.OK)
    public List<AllTickets> getAllTicketsAvailable() {
        return ticketService.getAllTicketsAvailable();
    }

    @GetMapping("ticket-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id)
            throws TicketServiceException, JsonProcessingException {
        return ResponseEntity.ok(ticketService.getById(id));
    }

    @GetMapping("{eventCode}")
    @Operation(summary = "Tickets for a particular event")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AllTickets>> getTicketByEventCode(@PathVariable String eventCode) {
        return ResponseEntity.ok(ticketService.getTicketByEventCode(eventCode));
    }

    @PutMapping("update-ticket/{ticketCode}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> updateTicket(@RequestBody TicketRequest ticketRequest,
                                               @PathVariable String ticketCode,
                                               @RequestHeader("userRoles") String userRolesHeader) throws TicketServiceException {
        List<String> userRoles = Arrays.asList(userRolesHeader.split(","));
        if (userRoles.contains("ADMIN")) {
            ticketService.updateTicket(ticketRequest, ticketCode);
            return ResponseEntity.ok("Ticket details updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("delete-ticket/{ticketCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteTicket(@PathVariable String ticketCode, @RequestHeader("userRoles") String userRolesHeader)
            throws TicketServiceException {
        List<String> userRoles = Arrays.asList(userRolesHeader.split(","));
        if (userRoles.contains("ADMIN")) {
            ticketService.deleteTicket(ticketCode);
            return ResponseEntity.ok("No Content");
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("remaining-tickets/{eventCode}")
    @ResponseStatus(HttpStatus.OK)
    public int remainingTickets(@PathVariable String eventCode) {
        return ticketService.remainingTickets(eventCode);
    }


}
