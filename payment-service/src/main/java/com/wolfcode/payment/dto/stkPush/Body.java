package com.wolfcode.payment.dto.stkPush;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Body {

    @JsonProperty("stkCallback")
    private StkCallback stkCallback;
}