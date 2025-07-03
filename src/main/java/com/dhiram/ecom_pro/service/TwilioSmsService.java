package com.dhiram.ecom_pro.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import io.github.cdimascio.dotenv.Dotenv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {
    Dotenv dotenv = Dotenv.load();
    
    private final String authToken = dotenv.get("TWILIO_AUTH_TOKEN");

    private final String accountSid= dotenv.get("TWILIO_ACCOUNT_SID");

    @Value("${twilio.phone.number}")
    private String fromNumber;

    public void sendSms(String toNumber, String messageBody) {
        Twilio.init(accountSid, authToken);

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(toNumber),
                new com.twilio.type.PhoneNumber(fromNumber),
                messageBody
        ).create();

        System.out.println("SMS Sent! SID: " + message.getSid());
    }
}
