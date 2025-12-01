package com.kraken.krakenhax;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotificationJTest {

    @Test
    public void testNotificationJ() {
        NotificationJ notificationJ = new NotificationJ("Test Title", "Body", "sender123", null, "eventX", "recipient456", false);
        assert notificationJ.getTitle().equals("Test Title");
        assert notificationJ.getBody().equals("Body");
        assert notificationJ.getSender().equals("sender123");
        assert notificationJ.getEventID().equals("eventX");
        assert notificationJ.getRecipient().equals("recipient456");
        assert !notificationJ.isRead();

        notificationJ.setRead(true);
        assert notificationJ.isRead();

        notificationJ.setTitle("New Title");
        assert notificationJ.getTitle().equals("New Title");

        notificationJ.setBody("New Body");
        assert notificationJ.getBody().equals("New Body");

        notificationJ.setSender("New Sender");
        assert notificationJ.getSender().equals("New Sender");

        notificationJ.setEventID("New EventID");
        assert notificationJ.getEventID().equals("New EventID");

        notificationJ.setRecipient("New Recipient");
        assert notificationJ.getRecipient().equals("New Recipient");

    }
}
