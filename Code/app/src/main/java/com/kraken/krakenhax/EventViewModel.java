package com.kraken.krakenhax;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EventViewModel extends ViewModel {
    private static MutableLiveData<ArrayList<Event>> eventList;

    private FirebaseFirestore db;
    private CollectionReference eventCollection;


    public void addEvent(Event event) {
        ArrayList<Event> currentList = eventList.getValue();
        if (currentList != null) {
            currentList.add(event);
            eventList.setValue(currentList);
        }
    }
    public EventViewModel() {
        eventList = new MutableLiveData<>(new ArrayList<>());
        // Initialize the Firestore database and get the "Events" collection reference.
        db = FirebaseFirestore.getInstance();
        eventCollection = db.collection("Events");

        // Start listening for real-time updates from Firestore.
        addSnapshotListener();
    }

    private void addSnapshotListener() {
        // This listener will be active for the entire lifecycle of the ViewModel.
        eventCollection.addSnapshotListener((snapshots, error) -> {
            // Handle any potential errors from Firestore.
            if (error != null) {
                Log.e("Firestore", "Listen failed", error);
            }
            // If there are snapshots (documents), process them.
            if (snapshots != null) {
                ArrayList<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    // Convert each document into an Event object.
                    Event event = doc.toObject(Event.class);
                    event.setId(doc.getId());
                    events.add(event);
                }
                eventList.setValue(events);
            }
        });
    }
}
