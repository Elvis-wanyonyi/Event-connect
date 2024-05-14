package com.wolfcode.payment.dto;

public record RegisterUrlRequest(int ShortCode,

                                 String ResponseType,

                                 String ConfirmationURL,

                                 String ValidationURL) {
}
