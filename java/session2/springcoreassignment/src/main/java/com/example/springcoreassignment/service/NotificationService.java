package com.example.springcoreassignment.service;

import org.springframework.stereotype.Service;

import com.example.springcoreassignment.component.NotificationComponent;

@Service
public class NotificationService {

    private final NotificationComponent notificationComponent;

    // constructor-based dependency injection for NotificationComponent
    public NotificationService(NotificationComponent notificationComponent) {
        this.notificationComponent = notificationComponent;
    }

    // method to trigger notification
    public String triggerNotification() {
        return notificationComponent.sendNotification();
    }
}
