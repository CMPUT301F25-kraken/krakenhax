package com.kraken.krakenhax;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Notification;

import com.google.firebase.Timestamp;

import java.sql.Time;

public class NotificationTest {

    public NotificationJ notification = new NotificationJ();

    @Test
    public void testNotificationFields() {
        notification.setTitle("Test Notification");
        notification.setBody("This is a test notification.");
        notification.setSender("Test Sender");
        notification.setTimestamp(Timestamp.now());
        notification.setEventID("12345");
        notification.setRecipient("Test Recipient");
        notification.setRead(false);
        assertEquals("Test Notification", notification.getTitle());
        assertEquals("This is a test notification.", notification.getBody());
        assertEquals("Test Sender", notification.getSender());
        assertNotNull(notification.getTimestamp());
        assertEquals("12345", notification.getEventID());
        assertEquals("Test Recipient", notification.getRecipient());
        assertFalse(notification.isRead());
    }
}
