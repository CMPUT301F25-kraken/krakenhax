package com.kraken.krakenhax;

public class NotificationJ {
    private String title;
    private String body;
    private String sender;
    private String timestamp;
    private String eventID;
    private String recipient;

    public NotificationJ(String title, String body, String sender, String timestamp, String eventID, String recipient) {
        this.title = title;
        this.body = body;
        this.sender = sender;
        this.timestamp = timestamp;
        this.eventID = eventID;
        this.recipient = recipient;
    }

    public NotificationJ() {
        // Required empty public constructor
    }


    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getEventID() {
        return eventID;
    }

    public String getRecipient() {
        return recipient;
    }

}
