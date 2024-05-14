package com.wolfcode.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wolfcode.payment.config.MpesaConfig;
import com.wolfcode.payment.dto.*;
import com.wolfcode.payment.dto.stkPush.ExternalStkPushRequest;
import com.wolfcode.payment.dto.stkPush.InternalStkPushRequest;
import com.wolfcode.payment.dto.stkPush.StkPushSyncResponse;
import com.wolfcode.payment.utils.Constants;
import com.wolfcode.payment.utils.HelperUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

import static com.wolfcode.payment.utils.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DarajaService {

    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final MpesaConfig mpesaConfig;

    public TokenResponse getAccessToken() {

        String encodedCredentials = HelperUtility.toBase64String(String.format("%s:%s", mpesaConfig.getConsumerKey(),
                mpesaConfig.getConsumerSecret()));

        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfig.getOauthEndpoint(), mpesaConfig.getGrantType()))
                .get()
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BASIC_AUTH_STRING, encodedCredentials))
                .addHeader(CACHE_CONTROL_HEADER, CACHE_CONTROL_HEADER_VALUE)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            return objectMapper.readValue(response.body().string(), TokenResponse.class);
        } catch (IOException e) {
            log.error(String.format("Could not get access token. -> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    public RegisterUrlResponse registerUrl() {
        TokenResponse tokenResponse = getAccessToken();
        String token = tokenResponse.getAccessToken();
        log.info("token >>>>>>: {}", token);

        RegisterUrlRequest registerUrlRequest = new RegisterUrlRequest(
                mpesaConfig.getShortCode(),
                mpesaConfig.getResponseType(),
                mpesaConfig.getConfirmationURL(),
                mpesaConfig.getValidationURL());

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(Objects.requireNonNull(HelperUtility.toJson(registerUrlRequest))));
        log.info("body------: {}", HelperUtility.toJson(registerUrlRequest));

        Request request = new Request.Builder()
                .url(mpesaConfig.getRegisterUrlEndpoint())
                .post(body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, token))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                log.info(String.format("Response >>>>> %s", response));
                return objectMapper.readValue(Objects.requireNonNull(response.body()).string(), RegisterUrlResponse.class);
            } else {
                log.error("Response body: {}", response.body() != null ? response.body().string() : "No response body");
                log.error("register url failed ");
                return null;
            }

        } catch (IOException e) {
            log.error(String.format("Could not register url ->>> %s", e.getLocalizedMessage()));
            return null;
        }
    }

    public C2bResponse simulateC2B(SimulateC2BRequest simulateC2BRequest) {
        TokenResponse tokenResponse = getAccessToken();
        String token = tokenResponse.getAccessToken();

        RequestBody requestBody = RequestBody.create(Constants.JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(simulateC2BRequest)));

        Request request = new Request.Builder()
                .url(mpesaConfig.getSimulateTransactionEndpoint())
                .method("POST", requestBody)
                .addHeader(AUTHORIZATION_HEADER_STRING,
                        String.format("%s %s", BEARER_AUTH_STRING, token))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;
            log.debug(String.format("Response ->>>>>>> %s", response));

            return objectMapper.readValue(response.body().string(), C2bResponse.class);
        } catch (IOException e) {
            log.error(String.format("unable to simulate the transaction %s", e.getLocalizedMessage()));
        }
        return null;
    }

    public StkPushSyncResponse performStkPushTransaction(InternalStkPushRequest internalStkPushRequest) {


        String transactionTimestamp = HelperUtility.getTransactionTimestamp();
        String stkPushPassword = HelperUtility.getStkPushPassword(mpesaConfig.getStkPushShortCode(),
                mpesaConfig.getStkPassKey(), transactionTimestamp);

        ExternalStkPushRequest externalStkPushRequest = ExternalStkPushRequest.builder()
                .businessShortCode(mpesaConfig.getStkPushShortCode())
                .password(stkPushPassword)
                .timestamp(transactionTimestamp)
                .transactionType(CUSTOMER_PAYBILL_ONLINE)
                .amount(internalStkPushRequest.getAmount())
                .partyA(internalStkPushRequest.getPhoneNumber())
                .partyB(mpesaConfig.getStkPushShortCode())
                .phoneNumber(internalStkPushRequest.getPhoneNumber())
                .callBackURL(mpesaConfig.getStkPushRequestCallbackUrl())
                .accountReference("Event-connect")
                .transactionDesc(String.format("->>>>> Transaction %s", internalStkPushRequest.getPhoneNumber()))
                .build();

        TokenResponse accessToken = getAccessToken();

        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE,
                Objects.requireNonNull(HelperUtility.toJson(externalStkPushRequest)));
        log.info(HelperUtility.toJson(externalStkPushRequest));
        Request request = new Request.Builder()
                .url(mpesaConfig.getStkPushRequestUrl())
                .method("POST", body)
                .addHeader(AUTHORIZATION_HEADER_STRING, String.format("%s %s", BEARER_AUTH_STRING, accessToken.getAccessToken()))
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            assert response.body() != null;

            return objectMapper.readValue(response.body().string(), StkPushSyncResponse.class);
        } catch (IOException e) {
            log.error(String.format("STK push transaction failed ->>>> %s", e.getLocalizedMessage()));
            return null;
        }


    }
}
