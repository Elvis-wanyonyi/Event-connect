package com.wolfcode.eventservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.eventservice.dto.*;
import com.wolfcode.eventservice.entity.Events;
import com.wolfcode.eventservice.entity.Reviews;
import com.wolfcode.eventservice.exception.EventNotFoundException;
import com.wolfcode.eventservice.feignClients.TicketClient;
import com.wolfcode.eventservice.repository.EventRepository;
import com.wolfcode.eventservice.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final ObjectMapper objectMapper;
    private final TicketClient ticketClient;
    private final ReviewRepository reviewRepository;


    @Transactional
    public EventResponse addNewEvent(CreateEventRequest eventRequest) throws JsonProcessingException {
        Events event = Events.builder()
                .eventCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase())
                .eventName(eventRequest.getEventName())
                .description(eventRequest.getDescription())
                .location(eventRequest.getLocation())
                .eventCategory(eventRequest.getEventCategory())
                .organizer(eventRequest.getOrganizer())
                .date(eventRequest.getDate())
                .time(eventRequest.getTime())
                .build();
        eventRepository.save(event);

        String eventJson = objectMapper.writeValueAsString(event);
        return objectMapper.readValue(eventJson, EventResponse.class);
    }

    public List<EventListing> getUpcomingEvents(int pageNo) {
        LocalDate today = LocalDate.now();

        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("date").ascending());
        Page<Events> eventsPage = eventRepository.findAllByDateAfter(today, pageable);
        return eventsPage.map(this::mapToEventListing).stream().toList();
    }

    public List<EventListing> getRecentEvents(int pageNo) {
        LocalDate today = LocalDate.now();

        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("date").descending());
        Page<Events> eventsPage = eventRepository.findAllByDateBefore(today, pageable);
        return eventsPage.map(this::mapToEventListing).toList();
    }

    private EventListing mapToEventListing(Events event) {
        return EventListing.builder()
                .eventCode(event.getEventCode())
                .eventName(event.getEventName())
                .description(event.getDescription())
                .date(event.getDate())
                .time(event.getTime())
                .organizer(event.getOrganizer())
                .eventCategory(event.getEventCategory())
                .location(event.getLocation())
                .build();
    }

    @Transactional
    public void updateEvent(UpdateEventRequest updateEventRequest, String eventCode) throws EventNotFoundException {
        Optional<Events> eventsOptional = eventRepository.findByEventCode(eventCode);
        if (eventsOptional.isPresent()) {
            Events events = eventsOptional.get();
            events.setDate(updateEventRequest.getDate());
            events.setDescription(updateEventRequest.getDescription());
            events.setLocation(updateEventRequest.getLocation());
            eventRepository.save(events);
        } else {
            throw new EventNotFoundException("Event not Found with name: " + eventCode);
        }
    }

    public EventResponse getEventById(Long id) throws JsonProcessingException, EventNotFoundException {
        Optional<Events> optionalEvents = eventRepository.findById(id);
        if (optionalEvents.isPresent()) {
            Events event = optionalEvents.get();
            String eventJson = objectMapper.writeValueAsString(event);
            return objectMapper.readValue(eventJson, EventResponse.class);
        } else
            throw new EventNotFoundException("Event doesn't exist with id: " + id);
    }

    public List<EventListing> getEventByCategory(EventCategory category, int pageNo) throws EventNotFoundException {
        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("date").ascending());

        Page<Events> eventsPage = eventRepository.findAllByEventCategory(category, pageable);
        if (eventsPage.isEmpty()) {
            throw new EventNotFoundException("No events on this Category");
        } else {
            return eventsPage.map(this::mapToEventListing).toList();
        }
    }

    public EventsTicket getEventTicketsByEventCode(String eventCode) throws EventNotFoundException {
        List<AllTickets> tickets = ticketClient.getTicketByEventCode(eventCode);
        Optional<Events> eventsOptional = eventRepository.findByEventCode(eventCode);
        if (eventsOptional.isPresent()) {
            Events event = eventsOptional.get();
            return EventsTicket.builder()
                    .description(event.getDescription())
                    .date(event.getDate())
                    .time(event.getTime())
                    .location(event.getLocation())
                    .organizer(event.getOrganizer())
                    .tickets(tickets)
                    .build();
        } else {
            throw new EventNotFoundException("Event not found with name: " + eventCode);
        }
    }

    @Transactional
    public void deleteByEventCode(String eventCode) throws EventNotFoundException {
        Optional<Events> event = eventRepository.findByEventCode(eventCode);
        if (event.isPresent()) {
            eventRepository.deleteByEventCode(eventCode);
        } else {
            throw new EventNotFoundException("Event not found with code: " + eventCode);
        }
    }

    //TODO Search towns and cities
    public List<EventListing> getEventByLocation(String location, int pageNo) throws EventNotFoundException {
        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("date").ascending());

        Page<Events> eventsPage = eventRepository.findByLocation(location, pageable);
        if (!eventsPage.isEmpty()) {
            return eventsPage.stream().map(this::mapToEventListing).collect(Collectors.toList());
        } else {
            throw new EventNotFoundException("No events available in your location");
        }
    }

    public List<EventListing> getEventByDate(LocalDate date, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("time").ascending());

        Page<Events> eventsPage = eventRepository.findByDate(date, pageable);
        return eventsPage.stream().map(this::mapToEventListing).collect(Collectors.toList());
    }

    public List<EventListing> getWeekendEvents(int pageNo) {
        LocalDate today = LocalDate.now();
        DayOfWeek currentDayOfWeek = today.getDayOfWeek();
        LocalDate nextSaturday = today.plusDays(DayOfWeek.SATURDAY.getValue() - currentDayOfWeek.getValue());
        LocalDate nextSunday = today.plusDays(DayOfWeek.SUNDAY.getValue() - currentDayOfWeek.getValue() + 7);

        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("date").ascending());
        Page<Events> weekend = eventRepository.findByDateBetween(nextSaturday, nextSunday,pageable);
        return weekend.map(this::mapToEventListing).toList();
    }

    public List<EventListing> getTodayEvents(int pageNo) {
        LocalDate today = LocalDate.now();

        Pageable pageable = PageRequest.of(pageNo -1, 20,
                Sort.by("date").ascending());
        Page<Events> events = eventRepository.findByDate(today, pageable);
        return events.stream().map(this::mapToEventListing).collect(Collectors.toList());
    }

    public void reviewEvent(ReviewEvent reviewEvent, String eventCode) throws EventNotFoundException {
        Optional<Events> eventsOptional = eventRepository.findByEventCode(eventCode);
        if (eventsOptional.isPresent()) {
            Events events = eventsOptional.get();
            Reviews addReview = Reviews.builder()
                    .email(reviewEvent.getEmail())
                    .rating(reviewEvent.getRating())
                    .review(reviewEvent.getReview())
                    .at(LocalDateTime.now())
                    .event(events)
                    .build();
            reviewRepository.save(addReview);
        } else {
            throw new EventNotFoundException("Event not found");
        }
    }

    public List<ReviewResponse> getEventReview(String eventCode) {

        List<Reviews> reviewsPage = reviewRepository.findByEvent_EventCode(eventCode);
        return reviewsPage.stream().unordered().map(this::mapToReviewResponse).collect(Collectors.toList());

    }

    private ReviewResponse mapToReviewResponse(Reviews review) {
        return ReviewResponse.builder()
                .email(review.getEmail())
                .rating(review.getRating())
                .review(review.getReview())
                .at(review.getAt())
                .build();
    }

    public void deleteByReviewId(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    public long getTotalEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.countByDateAfterOrDateEquals(today, today);
    }

    public void deleteReviewByEmail(String email) {
        reviewRepository.deleteByEmail(email);
    }

    public long getTotalReviewsOfEvent(String eventCode) {
        return reviewRepository.findByEvent_EventCode(eventCode).size();
    }

    public double getAverageRatingOfEvent(String eventCode) {
        List<Reviews> reviews = reviewRepository.findByEvent_EventCode(eventCode);

        Map<String, Integer> userRatings = new HashMap<>();
        for (Reviews review : reviews) {
            userRatings.put(review.getEmail(), review.getRating());
        }

        double sum = 0;
        for (int rating : userRatings.values()) {
            sum += rating;
        }
        return sum / userRatings.size();
    }

    public List<MyEvents> getEventByOrganizer(String organizer, int pageNo) {
        Pageable pageable = PageRequest.of(pageNo -1, 10,
                Sort.by("date").ascending());

        Page<Events> eventsPage = eventRepository.findByOrganizer(organizer,pageable);
        return eventsPage.stream().map(this::mapToMyEvents).collect(Collectors.toList());
    }

    private MyEvents mapToMyEvents(Events events) {
        return MyEvents.builder()
                .eventName(events.getEventName())
                .description(events.getDescription())
                .date(events.getDate())
                .time(events.getTime())
                .location(events.getLocation())
                .eventCategory(events.getEventCategory())
                .build();
    }

    public EventResponse getEventByEvenCode(String evenCode) throws JsonProcessingException, EventNotFoundException {
        Optional<Events> optionalEvents = eventRepository.findByEventCode(evenCode);
        if (optionalEvents.isPresent()) {
            Events event = optionalEvents.get();
            String eventJson = objectMapper.writeValueAsString(event);
            return objectMapper.readValue(eventJson, EventResponse.class);
        } else
            throw new EventNotFoundException("Event doesn't exist with id: " + evenCode);
    }


}
