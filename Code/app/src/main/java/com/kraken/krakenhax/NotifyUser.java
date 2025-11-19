package com.kraken.krakenhax;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.List;


/**
 * Handles system and in-app notifications.
 */
public class NotifyUser {
    private final Context context;

    public NotifyUser(Context context) {
        this.context = context;
    }

    /**
     * Sends a real Android notification to the user.
     */
    public void sendNotification(Profile recipient, String message) {
        if (recipient == null || message == null) return;

        // Check opt-out preference
        if (!recipient.isNotificationsEnabled()) {
            Log.d("NotifyUser", "Skipped notification for " + recipient.getUsername());
            Toast.makeText(context, recipient.getUsername() + " has notifications off", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show in-app toast for instant feedback
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "kraken_channel")
                .setSmallIcon(R.drawable.outline_attractions_100)
                .setContentTitle("🎉 " + recipient.getUsername() + ", new event update!")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    /**
     * Sends one message to multiple recipients.
     */
    public void sendBroadcast(List<Profile> recipients, String message) {
        if (recipients == null || message == null) return;
        for (Profile entrant : recipients) {
            sendNotification(entrant, message);
        }
    }

}
