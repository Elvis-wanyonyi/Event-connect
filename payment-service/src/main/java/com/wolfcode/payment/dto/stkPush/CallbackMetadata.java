package com.wolfcode.payment.dto.stkPush;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CallbackMetadata {

    @JsonProperty("Item")
    private List<ItemItem> item;
}