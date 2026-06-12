package com.assignment.todo5.client;

import org.junit.jupiter.api.Test;

public class NotificationServiceClientTest {

    @Test
    void testNotification() {

        NotificationServiceClient client = new NotificationServiceClient();

        client.sendNotification("Test");

        // no assert needed, just execution
    }
}