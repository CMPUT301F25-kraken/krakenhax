package com.kraken.krakenhax;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/**
 * Simple tests for NotifyUser class.
 * Verifies notification logic runs without errors.
 *
 * Author: Amaan
 */
public class NotifyUserTest {

    private NotifyUser notifier;
    private Profile recipient;

    @Before
    public void setUp() {
        notifier = new NotifyUser();
        recipient = new Profile("TestUser", "password123", "entrant", "test@example.com");

    }

    @Test
    public void testSendNotificationDoesNotCrash() {
        notifier.sendNotification(recipient, "Hello there!");
    }

    @Test
    public void testSendBroadcastDoesNotCrash() {
        ArrayList<Profile> list = new ArrayList<>();
        list.add(recipient);
        list.add(new Profile("User2", "pass", "entrant", "user2@example.com"));

        notifier.sendBroadcast(list, "Broadcast test");
    }

    @Test
    public void testSendNotificationHandlesNullsGracefully() {
        notifier.sendNotification(null, "Test");
        notifier.sendNotification(recipient, null);
        notifier.sendBroadcast(null, "Message");
        notifier.sendBroadcast(new ArrayList<>(), null);
    }
}
