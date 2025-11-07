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
 * @author Amaan
 * @version 1.2 — Added opt-out support for entrants
 */
public class NotifyUser {

    /**
     * Sends a notification to the specified entrant,
     * respecting their notification preferences.
     *
     * @param recipient the entrant receiving the notification
     * @param message   the message body
     */
    public void sendNotification(Profile recipient, String message) {
        if (recipient == null || message == null) return;

        // opt-out preference
        if (!recipient.isNotificationsEnabled()) {
            Log.d("NotifyUser", "Skipped notification for " + recipient.getUsername() + " (opted out)");
            return;
        }

        // Send notification
        try {
            Log.d("NotifyUser", "Notification to " + recipient.getUsername() + ": " + message);
        } catch (Exception e) {
            System.out.println("[NotifyUser] " + recipient.getUsername() + ": " + message);
        }
    }

    /**
     * Sends a single notification to multiple entrants,
     * skipping those who have opted out.
     *
     * @param recipients list of entrants to notify
     * @param message    the message body
     */
    public void sendBroadcast(List<Profile> recipients, String message) {
        if (recipients == null || message == null) return;
        for (Profile entrant : recipients) {
            sendNotification(entrant, message);
        }
    }
}
