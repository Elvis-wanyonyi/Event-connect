package com.wolfcode.ticketservice.feignClients;

import com.wolfcode.ticketservice.dto.EventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "EVENTS", path = "/api/v1/events")
public interface EventClient {


    @GetMapping("/eventCode/{evenCode}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<EventResponse> getEventByEvenCode(@PathVariable String evenCode);
}
