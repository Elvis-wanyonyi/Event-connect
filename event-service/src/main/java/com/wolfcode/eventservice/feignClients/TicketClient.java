package com.wolfcode.eventservice.feignClients;

import com.wolfcode.eventservice.dto.AllTickets;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "TICKET-SERVICE", path = "/api/v1/tickets/")
public interface TicketClient {

    @GetMapping("{eventCode}")
    List<AllTickets> getTicketByEventCode(@PathVariable String eventCode);


}
