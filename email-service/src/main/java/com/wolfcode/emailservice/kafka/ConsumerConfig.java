package com.wolfcode.emailservice.kafka;

import com.wolfcode.emailservice.dto.EmailRequest;
import com.wolfcode.emailservice.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ConsumerConfig {

    private final EmailService emailService;

    public ConsumerConfig(EmailService emailService) {
        this.emailService = emailService;
    }


    @KafkaListener(topics = "user-signup", groupId = "emailGroup")
    public void consumeUserSignup(EmailRequest emailRequest) {

        try {
            log.info("JsonPayload Message received {}", emailRequest.toString());

            emailService.sendSimpleMessage(emailRequest.getFrom(), emailRequest.getTo(),
                    emailRequest.getSubject(), emailRequest.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Unable to send mail :" + e.getLocalizedMessage());
        }
    }
}
