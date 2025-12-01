package com.kraken.krakenhax;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;


/**
 * Simple tests for NotifyUser class.
 * Verifies notification logic runs without errors.
 *
 */
public class NotifyUserTest {
    private NotifyUser notifier;
    private Profile recipient;
    private Context context;

    /**
     * Sets up common test data before each test execution.
     */
    @Before
    public void setUp() {
        //notifier = new NotifyUser();
        recipient = new Profile("6", "TestUser", "password123", "entrant", "test@example.com", "0");

    }

    /**
     * Ensures sending a single notification completes without crashing.
     */
    @Test
    public void testSendNotificationDoesNotCrash() {
        notifier.sendNotification(recipient, "Hello there!");
    }

    /**
     * Ensures sending a broadcast notification completes without crashing.
     */
    @Test
    public void testSendBroadcastDoesNotCrash() {
        ArrayList<Profile> list = new ArrayList<>();
        list.add(recipient);
        list.add(new Profile("7", "User2", "pass", "entrant", "user2@example.com", "0"));

        notifier.sendBroadcast(list, "Broadcast test");
    }

    /**
     * Ensures notification methods handle null arguments safely.
     */
    @Test
    public void testSendNotificationHandlesNullsGracefully() {
        notifier.sendNotification(null, "Test");
        notifier.sendNotification(recipient, null);
        notifier.sendBroadcast(null, "Message");
        notifier.sendBroadcast(new ArrayList<>(), null);
    }

}
