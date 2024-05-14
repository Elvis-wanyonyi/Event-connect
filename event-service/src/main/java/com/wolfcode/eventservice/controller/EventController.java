package com.wolfcode.eventservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wolfcode.eventservice.dto.*;
import com.wolfcode.eventservice.exception.EventNotFoundException;
import com.wolfcode.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventService eventService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventResponse> addNewEvent(@RequestBody CreateEventRequest eventRequest)
            throws JsonProcessingException {
        return ResponseEntity.ok(eventService.addNewEvent(eventRequest));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventListing> getUpcomingEvents(@RequestParam(defaultValue = "1") int pageNo) {
        return eventService.getUpcomingEvents(pageNo);

    }

    @GetMapping("/recent-events")
    public List<EventListing> getRecentEvents(@RequestParam(defaultValue = "1") int pageNo) {
        return eventService.getRecentEvents(pageNo);
    }

    @GetMapping("/today")
    public List<EventListing> getTodayEvents(@RequestParam(defaultValue = "1") int pageNo) {
        return eventService.getTodayEvents(pageNo);
    }

    @PutMapping("/update/{eventCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> updateEvent(@PathVariable String eventCode, @RequestBody UpdateEventRequest updateEventRequest)
            throws EventNotFoundException {

        eventService.updateEvent(updateEventRequest, eventCode);
        return ResponseEntity.ok("Event details have been updated");
    }

    @GetMapping("/{evenCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventResponse> getEventByEvenCode(@PathVariable String evenCode)
            throws EventNotFoundException, JsonProcessingException {

        return ResponseEntity.ok(eventService.getEventByEvenCode(evenCode));
    }

    @GetMapping("/byId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id)
            throws EventNotFoundException, JsonProcessingException {

        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/category/{category}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventListing>> getEventByCategory(@PathVariable EventCategory category,
                                                                 @RequestParam(defaultValue = "1") int pageNo)
            throws EventNotFoundException {

        return ResponseEntity.ok(eventService.getEventByCategory(category,pageNo));
    }

    @GetMapping("/organizer/{organizer}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MyEvents>> getEventByOrganizer(@PathVariable String organizer,
                                                              @RequestParam(defaultValue = "1") int pageNo) {

        return ResponseEntity.ok(eventService.getEventByOrganizer(organizer, pageNo));
    }

    @GetMapping("/event-tickets/{eventCode}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventsTicket> getEventTicketsByEventCode(@PathVariable String eventCode)
            throws EventNotFoundException {

        return ResponseEntity.ok(eventService.getEventTicketsByEventCode(eventCode));
    }

    @DeleteMapping("/delete/{eventCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteByEventCode(@PathVariable String eventCode, @RequestHeader("userRoles") String userRolesHeader) throws EventNotFoundException {
        List<String> userRoles = Arrays.asList(userRolesHeader.split(","));
        if (userRoles.contains("ADMIN"))
            eventService.deleteByEventCode(eventCode);
        return "No Content";
    }

    @GetMapping("/search-location/{location}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<EventListing> getEventByLocation(@PathVariable String location, int pageNo) throws EventNotFoundException {
        return eventService.getEventByLocation(location, pageNo);
    }

    @GetMapping("/search-event-date/{date}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<EventListing> getEventByDate(@PathVariable LocalDate date, @RequestParam(defaultValue = "1") int pageNo) {
        return eventService.getEventByDate(date,pageNo);
    }

    @GetMapping("/this-weekend")
    @ResponseStatus(HttpStatus.FOUND)
    public List<EventListing> getWeekendEvents(@RequestParam(defaultValue = "1") int pageNo) {
        return eventService.getWeekendEvents(pageNo);
    }


    @GetMapping("/total-events")
    public long getTotalEvents(@RequestHeader("userRoles") String userRolesHeader) {
        List<String> userRoles = Arrays.asList(userRolesHeader.split(","));
        if (userRoles.contains("ADMIN")) {
            return eventService.getTotalEvents();
        } else {
            throw new RuntimeException("Access Denied");
        }
    }


}
