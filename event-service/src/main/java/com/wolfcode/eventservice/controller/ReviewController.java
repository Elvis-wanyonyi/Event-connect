package com.wolfcode.eventservice.controller;

import com.wolfcode.eventservice.dto.ReviewEvent;
import com.wolfcode.eventservice.dto.ReviewResponse;
import com.wolfcode.eventservice.exception.EventNotFoundException;
import com.wolfcode.eventservice.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/reviews")
public class ReviewController {

    private final EventService eventService;

    @PostMapping("/{eventCode}/add-review")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> reviewEvent(@PathVariable String eventCode, @RequestBody ReviewEvent reviewEvent) throws EventNotFoundException {
        eventService.reviewEvent(reviewEvent, eventCode);
        return ResponseEntity.ok("Thanks for the feedback");
    }

    @GetMapping("/{eventCode}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> getEventReview(@PathVariable String eventCode) {
        return eventService.getEventReview(eventCode);
    }

    @DeleteMapping("/delete-review/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByReviewId(@PathVariable Long reviewId) {
        eventService.deleteByReviewId(reviewId);
    }

    @DeleteMapping("/delete-review/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByEmail(@PathVariable String email) {
        eventService.deleteReviewByEmail(email);
    }

    /* ==================REVIEW EVENT ANALYTICS ================*/
    @GetMapping("/total-reviews/{eventCode}")
    public long getTotalReviewsOfEvent(@PathVariable String eventCode) {
        return eventService.getTotalReviewsOfEvent(eventCode);
    }

    @GetMapping("/average-rating/{eventCode}")
    public double getAverageReviewsOfEvent(@PathVariable String eventCode) {
        return eventService.getAverageRatingOfEvent(eventCode);
    }

}
