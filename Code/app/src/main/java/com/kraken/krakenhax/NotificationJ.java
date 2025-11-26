package com.kraken.krakenhax;

import com.google.firebase.Timestamp;;

public class NotificationJ {
    private String title;
    private String body;
    private String sender;
    private Timestamp timestamp;
    private String eventID;
    private String recipient;

    private boolean read;

    //public NotificationJ() { }

    public NotificationJ(String title, String body, String sender, Timestamp timestamp, String eventID, String recipient, boolean read) {
        this.title = title;
        this.body = body;
        this.sender = sender;
        this.timestamp = timestamp;
        this.eventID = eventID;
        this.recipient = recipient;
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getEventID() {
        return eventID;
    }

    public String getRecipient() {
        return recipient;
    }

}
