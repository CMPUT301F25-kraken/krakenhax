package com.kraken.krakenhax;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.List;

/**
 * Handles in-app and system notifications for entrants and organizers.
 * Now supports visible Android notifications.
 */
public class NotifyUser {

    private final Context context;

    public NotifyUser(Context context) {
        this.context = context;
    }

    /**
     * Sends a system notification to the specified entrant.
     *
     * @param recipient Profile receiving the notification
     * @param message   message content
     */
    public void sendNotification(Profile recipient, String message) {
        if (recipient == null || message == null) return;

        if (!recipient.isNotificationsEnabled()) {
            Log.d("NotifyUser", "Skipped notification for " + recipient.getUsername() + " (opted out)");
            return;
        }

        // Intent to open the app when notification clicked
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "kraken_channel")
                .setSmallIcon(R.drawable.outline_attractions_100) // your app icon
                .setContentTitle("KrakenHax Notification")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Sends one message to multiple entrants.
     */
    public void sendBroadcast(List<Profile> recipients, String message) {
        if (recipients == null || message == null) return;
        for (Profile entrant : recipients) {
            sendNotification(entrant, message);
        }
    }
}
