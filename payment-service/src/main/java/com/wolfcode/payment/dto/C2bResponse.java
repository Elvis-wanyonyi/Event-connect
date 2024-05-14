package com.wolfcode.payment.dto;

public record C2bResponse(
        String OriginatorCoversationID,
        String ResponseCode,
        String ResponseDescription) {
}

