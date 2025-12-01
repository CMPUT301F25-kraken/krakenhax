package com.kraken.krakenhax;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Simple repository wrapper for Event-related Firestore operations.
 * Makes database logic testable with Mockito.
 */
public class EventRepository {
    private final FirebaseFirestore db;

    /**
     * Creates a new {@code EventRepository} that uses the given Firestore instance.
     *
     * @param db the {@link FirebaseFirestore} instance used for all event operations
     */
    public EventRepository(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Creates or updates the given event document in the {@code Events} collection.
     * If the event or its ID is {@code null}, the operation is skipped.
     *
     * @param event the event to persist in Firestore
     */
    public void saveEvent(Event event) {
        if (event == null || event.getId() == null) return;
        CollectionReference eventsRef = db.collection("Events");
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.set(event);
    }

    /**
     * Deletes the given event document from the {@code Events} collection.
     * If the event or its ID is {@code null}, the operation is skipped.
     *
     * @param event the event to remove from Firestore
     */
    public void deleteEvent(Event event) {
        if (event == null || event.getId() == null) return;
        CollectionReference eventsRef = db.collection("Events");
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.delete();
    }

}
