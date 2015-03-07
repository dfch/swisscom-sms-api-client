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
import org.springframework.web.client.RestTemplate;
import web.rufer.swisscom.sms.api.domain.OutboundSMSMessageRequest;
import web.rufer.swisscom.sms.api.domain.OutboundSMSTextMessage;
import web.rufer.swisscom.sms.api.domain.CommunicationWrapper;
import web.rufer.swisscom.sms.api.factory.HeaderFactory;
import web.rufer.swisscom.sms.api.validation.AbstractValidator;
import web.rufer.swisscom.sms.api.validation.PhoneNumberValidator;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

public class SwisscomSmsSender {

    private static final String API_URI_PREFIX = "https://api.swisscom.com/v1/messaging/sms/outbound/tel%3A%2B";
    private static final String API_URI_SUFFIX = "/requests";
    private static final String NUMBER_PREFIX = "tel:";
    private static final String DELIMITER = "";

    private String apiKey;
    private String senderNumber;
    private String senderName;
    private String clientCorrelator;
    private AbstractValidator validator = new PhoneNumberValidator();
    protected RestTemplate restTemplate;

    /**
     * Constructor
     *
     * @param apiKey the API-key generated by http://developer.swisscom.com
     * @param senderNumber the number of the sender (i.e. +41791234567)
     */
    public SwisscomSmsSender(String apiKey, String senderNumber) {
        validator.validate(senderNumber);
        this.apiKey = apiKey;
        this.senderNumber = senderNumber;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Extended Constructor
     *
     * @param apiKey the API-key generated by http://developer.swisscom.com
     * @param senderNumber the number of the sender (i.e. +41791234567)
     * @param senderName [ONLY IN PARTNER MODE] Name of the sender, which should be displayed on the receivers phone
     * @param clientCorrelator An id that can be found in the logs of Swisscom
     */
    public SwisscomSmsSender(String apiKey, String senderNumber, String senderName, String clientCorrelator) {
        validator.validate(senderNumber);
        this.apiKey = apiKey;
        this.senderNumber = senderNumber;
        this.senderName = senderName;
        this.clientCorrelator = clientCorrelator;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send a sms to one or more receivers
     *
     * @param message the message text
     * @param receiverNumbers the numbers of the receivers (i.e. +41791234567)
     */
    public CommunicationWrapper sendSms(String message, String... receiverNumbers) {
        validator.validate(receiverNumbers);
        CommunicationWrapper communicationWrapper = new CommunicationWrapper();
        communicationWrapper.setOutboundSMSMessageRequest(createOutboundSMSMessageRequest(message, receiverNumbers));
        return restTemplate.postForObject(createRequestUri(), new HttpEntity(communicationWrapper, HeaderFactory.createHeaders(apiKey)), CommunicationWrapper.class);
    }

    protected OutboundSMSMessageRequest createOutboundSMSMessageRequest(String message, String[] receiverNumbers) {
        OutboundSMSMessageRequest smsMessageRequest = new OutboundSMSMessageRequest();
        smsMessageRequest.setSenderAddress(String.join(DELIMITER, NUMBER_PREFIX, senderNumber));
        smsMessageRequest.setAddress(prefixAndAddReceiverNumbersToList(receiverNumbers));
        smsMessageRequest.setOutboundSMSTextMessage(new OutboundSMSTextMessage(message));
        smsMessageRequest.setSenderName(senderName);
        smsMessageRequest.setClientCorrelator(clientCorrelator);
        return smsMessageRequest;
    }

    protected List<String> prefixAndAddReceiverNumbersToList(String[] receiverNumbers) {
        List<String> receivers = new LinkedList();
        for (String receiverNumber : receiverNumbers) {
            receivers.add(String.join(DELIMITER, NUMBER_PREFIX, receiverNumber));
        }
        return receivers;
    }

    protected URI createRequestUri() {
        return URI.create(String.join(DELIMITER, API_URI_PREFIX, senderNumber.substring(1), API_URI_SUFFIX));
    }
}