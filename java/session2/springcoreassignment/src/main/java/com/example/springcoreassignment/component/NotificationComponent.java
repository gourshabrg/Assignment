package com.example.springcoreassignment.component;

import org.springframework.stereotype.Component;

@Component
public class NotificationComponent {

    // method to send notification
    public String sendNotification() {
        return "Notification Sent";
    }
}
