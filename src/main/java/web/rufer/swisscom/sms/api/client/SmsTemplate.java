/*
 * Copyright (C) 2015 Marc Rufer (m.rufer@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package web.rufer.swisscom.sms.api.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import web.rufer.swisscom.sms.api.domain.OutboundSMSMessageRequest;
import web.rufer.swisscom.sms.api.domain.OutboundSMSTextMessage;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

public class SmsTemplate {

    private static final String CLIENT_ID = "client_id";
    public static final String API_URI = "https://api.swisscom.com/v1/messaging/sms/outbound/tel%3A%2B{0}/requests";

    private String apiKey;
    private String senderNumber;
    protected RestTemplate restTemplate;

    /**
     * Constructor
     *
     * @param apiKey the API key from developer.swisscom.com
     * @param senderNumber the number of the sender (i.e. +41791234567)
     */
    public SmsTemplate(String apiKey, String senderNumber) {
        this.apiKey = apiKey;
        this.senderNumber = senderNumber;
        restTemplate = new RestTemplate();
    }

    public void sendSms(String message, String... receivers) {
        OutboundSMSMessageRequest requestBody = new OutboundSMSMessageRequest();
        requestBody.setSenderAddress(senderNumber);
        ArrayList<String> receiverList = new ArrayList<>();
        for (String receiver : receivers) {
            receiverList.add(receiver);
        }
        requestBody.setAddress(receiverList);
        requestBody.setOutboundSMSTextMessage(new OutboundSMSTextMessage(message));
        URI uri = URI.create(String.format(API_URI, senderNumber.substring(1)));
        restTemplate.postForObject(uri, new HttpEntity(requestBody, createHeaders()), HttpEntity.class);
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(CLIENT_ID, apiKey);
        return headers;
    }
}