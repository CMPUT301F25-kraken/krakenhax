package com.kraken.krakenhax;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class EventViewModel extends ViewModel {
    private static MutableLiveData<ArrayList<Event>> eventList;

    private final FirebaseFirestore db;
    private final CollectionReference eventCollection;
    private final StorageReference storageRef;

    public void addEvent(Event event) {
        // First, add the event to the local list for immediate UI update.
        ArrayList<Event> currentList = eventList.getValue();
        if (currentList != null) {
            currentList.add(event);
            eventList.setValue(currentList);
        }
        // Then, upload the event to Firestore.
        uploadEvent(event);
    }
    public EventViewModel() {
        eventList = new MutableLiveData<>(new ArrayList<>());
        // Initialize the Firestore database and get the "Events" collection reference.
        db = FirebaseFirestore.getInstance();
        eventCollection = db.collection("Events");
        storageRef = FirebaseStorage.getInstance().getReference();
        // Start listening for real-time updates from Firestore.
        addSnapshotListener();
    }


    public void uploadPosterForEvent(Event event, Uri filePath) {
        if (filePath != null && event != null && event.getId() != null) {
            StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");
            UploadTask uploadTask = eventPosterRef.putFile(filePath);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);
                    event.setPoster(downloadUrl);
                    db.collection("events")
                            .document(event.getId())
                            .set(event)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firebase", "Event poster uploaded successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firebase", "Error uploading event poster", e);
                            });
                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Upload failed", e);
            });
        } else {
            Log.e("Firebase", "Event or file path is null");
        }
    }

    private void uploadEvent(Event event) {
        if (event == null || event.getId() == null) {
            Log.e("Firebase", "Event or ID is null");
            return;
        }
        eventCollection.document(event.getId())
                .set(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Event added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Error adding event", e);
                });

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
                    try {
                        Event event = doc.toObject(Event.class);
                        if (event.getId() == null) {
                            event.setId(doc.getId());
                        }
                        events.add(event);
                    } catch (Exception e) {
                        Log.e("Firestore Deserialization", "Error converting document to Event object", e);
                    }

                }
                eventList.setValue(events);
            }
        });
    }
}