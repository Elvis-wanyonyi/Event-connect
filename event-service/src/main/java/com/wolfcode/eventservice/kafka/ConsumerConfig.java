package com.wolfcode.eventservice.kafka;

import com.wolfcode.eventservice.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@Slf4j
public class ConsumerConfig {

    private final EventService eventService;

    public ConsumerConfig(EventService eventService) {
        this.eventService = eventService;
    }

    @KafkaListener(topics = "user-deactivate-topic", groupId = "deactivateGroup")
    public void consumeUserDeactivateEmail(String email) {
        log.info(format("Consuming from user-deactivate-topic: %s", email));

        eventService.deleteReviewByEmail(email);
    }
}
