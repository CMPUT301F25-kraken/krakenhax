package com.kraken.krakenhax;

import com.google.firebase.Timestamp;

import java.io.Serializable;


/**
 * Notification class. Holds a notification that is serializable
 */
public class NotificationJ implements Serializable {
    public String title;
    private String body;
    private String sender;
    private Timestamp timestamp;
    private String eventID;
    private String recipient;
    private boolean read;

    /**
     * Empty Constructor for the notification.
     * Note: Needed for firebase, DON'T REMOVE!
     */
    public NotificationJ() { }

    /**
     * Constructor for notification class.
     * @param title String variable for the title of the notification
     * @param body  String variable for the main text of the notification
     * @param sender String variable for the Username of the sender
     * @param timestamp Timestamp variable for when the notification was created
     * @param eventID String variable for the eventID
     * @param recipient String variable for the recipient username
     * @param read Boolean variable to show if the user has read the notification
     */
    public NotificationJ(String title, String body, String sender, Timestamp timestamp, String eventID, String recipient, boolean read) {
        this.title = title;
        this.body = body;
        this.sender = sender;
        this.timestamp = timestamp;
        this.eventID = eventID;
        this.recipient = recipient;
        this.read = read;
    }

    /**
     * Gets the read boolean for a notification
     * @return Returns the boolean value.
     */
    public boolean isRead() {
        return read;
    }

    /**
     * sets the read boolean
     * @param read takes a boolean value to set the variable to
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * gets the title string of a notification
     * @return Returns the title string.
     */

    public String getTitle() {
        return title;
    }

    /**
     * gets the notification body string
     * @return Returns the body string variable
     */
    public String getBody() {
        return body;
    }

    /**
     * gets the notification sender string
     * @return returns the sender string variable of the senders username
     */
    public String getSender() {
        return sender;
    }

    /**
     * gets the notification timestamp
     * @return Returns the Timestamp variable of the notification
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * gets the event ID string
     * @return returns the string of the event ID variable
     */
    public String getEventID() {
        return eventID;
    }

    /**
     * Gets the recipient's username string
     * @return returns the recipient string variable
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the title string of a notification
     * @param title Takes a string to set the title to.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * sets the body string of a notification
     * @param body Takes a string message to set the variable to
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Sets the sender string of a notification
     * @param sender Takes a string of the senders username
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Sets the timestamp variable of a notification
     * @param timestamp Takes a timestamp to set the variable to
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the EventID string of a notification
     * @param eventID Takes an event ID string to set the variable to.
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Sets the recipient string of a notification
     * @param recipient Takes a string of a username to set the variable to
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

}
