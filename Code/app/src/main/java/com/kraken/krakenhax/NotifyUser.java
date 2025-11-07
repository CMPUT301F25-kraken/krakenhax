package com.kraken.krakenhax;

import android.util.Log;

import java.util.List;

/**
 * Handles notification logic within the app.
 * Sends in-app or database-based messages to entrants when event states change
 * (e.g., selected, cancelled, or event updates).
 *
 * <p>Collaborators: Entrant, Organizer, Event, NotifyLog</p>
 *
 * @version 1.1
 */
public class NotifyUser {

    /**
     * Sends a notification to the specified entrant.
     * @param recipient the entrant receiving the notification
     * @param message the message body
     */
    public void sendNotification(Profile recipient, String message) {
        if (recipient == null || message == null) return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.DONUT) {
            try {
                Log.d("NotifyUser", "Notification to " + recipient.getUsername() + ": " + message);
            } catch (Exception e) {
                System.out.println("[NotifyUser] " + recipient.getUsername() + ": " + message);
            }
        } else {
            System.out.println("[NotifyUser] " + recipient.getUsername() + ": " + message);
        }

    }

    /**
     * Sends a single notification to multiple entrants.
     * @param recipients list of entrants to notify
     * @param message the message body
     */
    public void sendBroadcast(List<Profile> recipients, String message) {
        if (recipients == null || message == null) return;
        for (Profile entrant : recipients) {
            sendNotification(entrant, message);
        }
    }
}
