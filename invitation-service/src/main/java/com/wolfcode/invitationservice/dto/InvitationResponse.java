package com.wolfcode.invitationservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InvitationResponse(
        String inviteCode,
        String recipient,
        String sender,
        String description,
        LocalDateTime sendAt,
        InvitationStatus status) {
}
