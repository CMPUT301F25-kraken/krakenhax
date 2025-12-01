package com.kraken.krakenhax;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit tests for NotificationJ model.
 */
public class NotificationJTest {

    @Test
    public void testNotificationInitialization() {
        Notification n = new Notification(
                "Title",
                "Body",
                "sender123",
                null,
                "eventABC",
                "recipient456",
                false
        );

        assertEquals("Title", n.getTitle());
        assertEquals("Body", n.getBody());
        assertEquals("sender123", n.getSender());
        assertEquals("eventABC", n.getEventID());
        assertEquals("recipient456", n.getRecipient());
        assertFalse(n.isRead());
    }

    @Test
    public void testMarkReadFlag() {
        Notification n = new Notification(
                "Title",
                "Body",
                "sender123",
                null,
                "eventABC",
                "recipient456",
                false
        );
        assertFalse(n.isRead());
        n.setRead(true);
        assertTrue(n.isRead());
    }
}
