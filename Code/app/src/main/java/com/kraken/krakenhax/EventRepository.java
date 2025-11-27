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

    public EventRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void saveEvent(Event event) {
        if (event == null || event.getId() == null) return;
        CollectionReference eventsRef = db.collection("Events");
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.set(event);
    }

    public void deleteEvent(Event event) {
        if (event == null || event.getId() == null) return;
        CollectionReference eventsRef = db.collection("Events");
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.delete();
    }
}
