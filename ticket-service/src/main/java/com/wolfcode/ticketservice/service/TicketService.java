package com.wolfcode.ticketservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.ticketservice.dto.AllTickets;
import com.wolfcode.ticketservice.dto.TicketRequest;
import com.wolfcode.ticketservice.dto.TicketResponse;
import com.wolfcode.ticketservice.entity.Ticket;
import com.wolfcode.ticketservice.exception.TicketServiceException;
import com.wolfcode.ticketservice.feignClients.EventClient;
import com.wolfcode.ticketservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final ObjectMapper objectMapper;
    private final TicketRepository ticketRepository;
    private final EventClient eventClient;


    public TicketResponse createEventTicket(TicketRequest ticketRequest, String eventCode) throws JsonProcessingException, TicketServiceException {
        var event = eventClient.getEventByEvenCode(eventCode);
        if (event != null) {
            Ticket ticket = Ticket.builder()
                    .ticketCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                    .eventCode(eventCode)
                    .ticketType(ticketRequest.getTicketType())
                    .price(ticketRequest.getPrice())
                    .quantity(ticketRequest.getQuantity())
                    .deadline(ticketRequest.getDeadline())
                    .build();
            ticketRepository.save(ticket);
            String JsonTicket = objectMapper.writeValueAsString(ticket);
            return objectMapper.readValue(JsonTicket, TicketResponse.class);
        } else {
            throw new TicketServiceException("Event not found");
        }
    }

    public void updateTicket(TicketRequest ticketRequest, String ticketCode) throws TicketServiceException {
        Ticket ticket = ticketRepository.findByTicketCodeIgnoreCase(ticketCode);
        if (ticket != null) {

            ticket.setTicketType(ticketRequest.getTicketType());
            ticket.setPrice(ticketRequest.getPrice());
            ticket.setQuantity(ticketRequest.getQuantity());
            ticket.setDeadline(ticketRequest.getDeadline());

            ticketRepository.save(ticket);
        } else {
            throw new TicketServiceException("Ticket not found with id: " + ticketCode);
        }
    }

    public void deleteTicket(String ticketCode) throws TicketServiceException {
        Ticket ticket = ticketRepository.findByTicketCodeIgnoreCase(ticketCode);
        if (ticket != null) {
            ticketRepository.deleteByTicketCode(ticketCode);
        } else {
            throw new TicketServiceException("Ticket not found with id: " + ticketCode);
        }
    }

    public TicketResponse getById(Long id) throws JsonProcessingException, TicketServiceException {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if (ticket.isPresent()) {
            Ticket optionalTicket = ticket.get();
            String ticketJson = objectMapper.writeValueAsString(optionalTicket);
            return objectMapper.readValue(ticketJson, TicketResponse.class);
        } else {
            throw new TicketServiceException("Ticket not found with id: " + id);
        }
    }

    public List<AllTickets> getTicketByEventCode(String eventCode) {
        List<Ticket> ticket = ticketRepository.findByEventCode(eventCode);

        return ticket.stream().map(this::mapToAllTickets).collect(Collectors.toList());

    }

    public List<AllTickets> getAllTicketsAvailable() {
        List<Ticket> ticket = ticketRepository.findAll();
        return ticket.stream().map(this::mapToAllTickets).collect(Collectors.toList());
    }

    private AllTickets mapToAllTickets(Ticket tickets) {
        return AllTickets.builder()
                .ticketType(tickets.getTicketType())
                .eventCode(tickets.getEventCode())
                .ticketCode(tickets.getTicketCode())
                .price(tickets.getPrice())
                .deadline(tickets.getDeadline())
                .quantity(tickets.getQuantity())
                .build();
    }

    public int remainingTickets(String eventCode) {
        List<Ticket> ticket = ticketRepository.findByEventCode(eventCode);
        return ticket.size();
    }

}
