package com.notification.service.app.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailSender {

    public void sendEmail(String orderNumber) {
        log.info("Order Placed Successfully - Order Number is {}", orderNumber);
        log.info("Email Sent For Order Id {}", orderNumber);
    }
}