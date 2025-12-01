package com.kraken.krakenhax;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTest {

    @Test
    public void testNotificationJ() {
        Notification notification = new Notification("Test Title", "Body", "sender123", null, "eventX", "recipient456", false);
        assert notification.getTitle().equals("Test Title");
        assert notification.getBody().equals("Body");
        assert notification.getSender().equals("sender123");
        assert notification.getEventID().equals("eventX");
        assert notification.getRecipient().equals("recipient456");
        assert !notification.isRead();

        notification.setRead(true);
        assert notification.isRead();

        notification.setTitle("New Title");
        assert notification.getTitle().equals("New Title");

        notification.setBody("New Body");
        assert notification.getBody().equals("New Body");

        notification.setSender("New Sender");
        assert notification.getSender().equals("New Sender");

        notification.setEventID("New EventID");
        assert notification.getEventID().equals("New EventID");

        notification.setRecipient("New Recipient");
        assert notification.getRecipient().equals("New Recipient");

    }
}
