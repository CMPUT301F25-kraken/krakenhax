package com.kraken.krakenhax;

import com.google.firebase.Timestamp;
import java.io.Serializable;

public class NotificationJ implements Serializable {
    public String title;
    private String body;
    private String sender;
    private Timestamp timestamp;
    private String eventID;
    private String recipient;

    private boolean read;

    public NotificationJ() { }

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

    public void setTitle(String title) {
        this.title = title;
    }


    public void setBody(String body) {
        this.body = body;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }



}
