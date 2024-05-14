package com.wolfcode.ticketservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.ticketservice.dto.BuyTicket;
import com.wolfcode.ticketservice.dto.BuyTicketResponse;
import com.wolfcode.ticketservice.dto.EventHistory;
import com.wolfcode.ticketservice.dto.TicketTypeRevenue;
import com.wolfcode.ticketservice.dto.stkPush.InternalStkPushRequest;
import com.wolfcode.ticketservice.entity.Ticket;
import com.wolfcode.ticketservice.entity.TicketSale;
import com.wolfcode.ticketservice.exception.TicketServiceException;
import com.wolfcode.ticketservice.feignClients.EmailClient;
import com.wolfcode.ticketservice.feignClients.EmailRequest;
import com.wolfcode.ticketservice.feignClients.PaymentClient;
import com.wolfcode.ticketservice.repository.SaleTicketRepository;
import com.wolfcode.ticketservice.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleTicketService {

    private final SaleTicketRepository saleTicketRepository;
    private final TicketRepository ticketRepository;
    private final PaymentClient paymentClient;
    private final EmailClient emailClient;
    private final ObjectMapper objectMapper;


    @Transactional
    public BuyTicketResponse buyTicketNow(String ticketCode, BuyTicket buyTicket)
            throws TicketServiceException, JsonProcessingException {

        Ticket ticket = ticketRepository.findByTicketCodeIgnoreCase(ticketCode);
        if (ticket != null && ticket.getQuantity() >= 1) {
            TicketSale saleTicket = TicketSale.builder()
                    .saleCode(UUID.randomUUID().toString().toUpperCase().substring(0, 7))
                    .ticketType(ticket.getTicketType())
                    .event(ticket.getEventCode())
                    .amount(ticket.getPrice())
                    .serialNo(ticket.getTicketType().substring(0, 3).toUpperCase() + UUID.randomUUID()
                            .toString().substring(0, 8).toUpperCase())
                    .ticket(ticket)
                    .email(buyTicket.getEmail())
                    .phone(buyTicket.getCountryCode() + buyTicket.getPhone())
                    .build();
            try {
                InternalStkPushRequest stkPushRequest = new InternalStkPushRequest();
                stkPushRequest.setPhoneNumber(saleTicket.getPhone());
                stkPushRequest.setAmount(saleTicket.getAmount());
                paymentClient.performStkPushTransaction(stkPushRequest);
            } catch (Exception e) {
                throw new TicketServiceException("unable process ticket payment try again ");
            }
            //TODO: should wait until the payment is confirmed before persisting & providing ticket
            saleTicket.setBoughtAt(LocalDateTime.now());
            saleTicketRepository.save(saleTicket);
            ticket.setQuantity(ticket.getQuantity() - 1);
            ticketRepository.save(ticket);

            String from = "Event connect";
            String to = saleTicket.getEmail();
            String subject = "Here's your ticket for the event";
            String body = "Checkout your ticket for the event " + objectMapper.writeValueAsString(BuyTicketResponse.class);
            emailClient.sendEmail(new EmailRequest(from, to, subject, body));

            return BuyTicketResponse.builder()
                    .ticketType(saleTicket.getTicketType())
                    .amount(saleTicket.getAmount())
                    .serialNo(saleTicket.getSerialNo())
                    .ticketCode(saleTicket.getSaleCode())
                    .build();

        } else {
            throw new TicketServiceException("Tickets are SOLD OUT or Ticket not found with id: " + ticketCode);
        }
    }

    public boolean checkIn(String serialNo) {
        TicketSale ticketSale = saleTicketRepository.findBySerialNo(serialNo);

        if (ticketSale != null && !ticketSale.isUsed()) {
            ticketSale.setUsed(true);
            ticketSale.setCheckInTime(LocalDateTime.now());
            saleTicketRepository.save(ticketSale);
            return true;
        }
        return false;
    }

    public void validateTicket(String serialNo) throws TicketServiceException {
        TicketSale ticketSale = saleTicketRepository.findBySerialNo(serialNo);
        if (ticketSale == null) {
            throw new TicketServiceException("Ticket not found with code: " + serialNo);
        }
    }

    @Transactional
    public boolean revokeTicket(String ticketCode, String serialNo) throws TicketServiceException {
        Ticket ticket = ticketRepository.findByTicketCodeIgnoreCase(ticketCode);
        TicketSale ticketSale = saleTicketRepository.findBySerialNo(serialNo);

        if (ticketSale == null && ticket == null) {
            throw new TicketServiceException("Ticket not found. Check ticketCode and Try Again !");
        }
        if (LocalDate.now().isBefore(ticket.getDeadline())) {
            assert ticketSale != null;
            if (!ticketSale.isRevoked()) {

                ticketSale.setRevoked(true);
                ticket.setQuantity(ticket.getQuantity() + 1);
                return true;
            }
        }
        return false;
    }

    public TicketTypeRevenue getRevenueForTicketType(String ticketCode) {
        List<TicketSale> sales = saleTicketRepository.findByTicket_ticketCode(ticketCode);

        List<TicketSale> validSales = sales.stream().filter(sale -> !sale.isRevoked()).toList();
        double totalRevenue = validSales.stream().mapToDouble(ticketSale -> Double.parseDouble(ticketSale.getAmount())).sum();

        String ticketName;
        if (validSales.isEmpty()) {
            ticketName = "Ticket Type (No Sales)";
        } else {
            ticketName = validSales.get(0).getTicket().getTicketType();  // Get name from first sale
        }
        return TicketTypeRevenue.builder()
                .ticketType(ticketName)
                .revenue(totalRevenue)
                .build();
    }

    public List<EventHistory> getEventHistory(String email) {
        List<TicketSale> ticketSale = saleTicketRepository.findByEmail(email);
        return ticketSale.stream()
                .map(this::mapToEventHistory).collect(Collectors.toList());

    }

    private EventHistory mapToEventHistory(TicketSale myTickets) {

        return EventHistory.builder()
                .eventName(myTickets.getEvent())
                .price(Double.parseDouble(myTickets.getAmount()))
                .ticketType(myTickets.getTicketType())
                .build();
    }
}
