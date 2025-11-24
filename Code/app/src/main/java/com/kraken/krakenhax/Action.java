package com.kraken.krakenhax;

import com.google.firebase.Timestamp;

public class Action {
    // Possible actions: join waitlist, withdraw from waitlist, accept place in event, decline place in event
    // won lottery for event, lost lottery for event, triggered event
    private String action;
    private String associatedUserID;
    private String associatedEventID;
    private Timestamp timestamp;

    // Firestore requires a public no-argument constructor
    public Action() {
        // Empty constructor needed for Firestore serialization
    }

    public Action(String action, String associatedUserID, String associatedEventID) {
        this.action = action;
        this.associatedUserID = associatedUserID;
        this.associatedEventID = associatedEventID;
        this.timestamp = Timestamp.now();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAssociatedUserID() {
        return associatedUserID;
    }

    public void setAssociatedUserID(String associatedUserID) {
        this.associatedUserID = associatedUserID;
    }

    public String getAssociatedEventID() {
        return associatedEventID;
    }

    public void setAssociatedEventID(String associatedEventID) {
        this.associatedEventID = associatedEventID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
