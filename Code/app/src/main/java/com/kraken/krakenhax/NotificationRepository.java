package com.kraken.krakenhax;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Repository for saving NotificationJ objects into Firestore.
 */
public class NotificationRepository {
    private final FirebaseFirestore db;

    /**
     * Creates a new repository instance using the provided Firestore database.
     *
     * @param db the {@link FirebaseFirestore} instance used to store notifications
     */
    public NotificationRepository(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Store a notification in the recipient's Profile subcollection:
     * Profiles/{recipientId}/Notifications/{autoId}
     */
    public void saveNotificationForProfile(NotificationJ notification) {
        if (notification == null || notification.getRecipient() == null) return;

        String recipientId = notification.getRecipient();
        CollectionReference notifRef = db.collection("Profiles")
                .document(recipientId)
                .collection("Notifications");

        notifRef.add(notification)
                .addOnSuccessListener(docRef ->
                        docRef.update("timestamp", FieldValue.serverTimestamp())
                );
    }

}
