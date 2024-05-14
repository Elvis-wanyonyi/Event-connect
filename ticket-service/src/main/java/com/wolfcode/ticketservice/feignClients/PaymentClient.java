package com.wolfcode.ticketservice.feignClients;

import com.wolfcode.ticketservice.dto.stkPush.InternalStkPushRequest;
import com.wolfcode.ticketservice.dto.stkPush.StkPushSyncResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE", path = "/api/payment/")
public interface PaymentClient {


    @PostMapping("stk-push")
    ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest);
}
