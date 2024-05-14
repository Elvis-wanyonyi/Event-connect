package com.wolfcode.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.payment.dto.*;
import com.wolfcode.payment.dto.stkPush.InternalStkPushRequest;
import com.wolfcode.payment.dto.stkPush.StkPushAsyncResponse;
import com.wolfcode.payment.dto.stkPush.StkPushSyncResponse;
import com.wolfcode.payment.service.DarajaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment/")
@Slf4j
public class MpesaController {

    private final DarajaService darajaService;
    private final AcknowledgeResponse acknowledgeResponse;
    private final ObjectMapper objectMapper;

    @GetMapping
    public String sayHello() {
        return "not saying !!";
    }

    @GetMapping("token")
    public ResponseEntity<TokenResponse> getAccessToken() {
        return ResponseEntity.ok(darajaService.getAccessToken());
    }

    @PostMapping("register-url")
    public ResponseEntity<RegisterUrlResponse> registerUrl() {
        return ResponseEntity.ok(darajaService.registerUrl());
    }

    @PostMapping("validation")
    public ResponseEntity<AcknowledgeResponse> validateTransaction(@RequestBody ValidationResponse validationResponse) {
        return ResponseEntity.ok(acknowledgeResponse);
    }

    @PostMapping("simulate-c2b")
    public ResponseEntity<C2bResponse> simulateC2B(@RequestBody SimulateC2BRequest simulateC2BRequest) {
        return ResponseEntity.ok(darajaService.simulateC2B(simulateC2BRequest));
    }

    @PostMapping("stk-push")
    public ResponseEntity<StkPushSyncResponse> performStkPushTransaction(@RequestBody InternalStkPushRequest internalStkPushRequest) {
        return ResponseEntity.ok(darajaService.performStkPushTransaction(internalStkPushRequest));
    }

    @PostMapping("stk-push-status")
    public ResponseEntity<AcknowledgeResponse> acknowledgeStkPushResponse(@RequestBody StkPushAsyncResponse stkPushAsyncResponse) throws JsonProcessingException {
        log.info("********** STK Push Async Response ***********");
        log.info(objectMapper.writeValueAsString(stkPushAsyncResponse));
        return ResponseEntity.ok(acknowledgeResponse);
    }
}
