package com.kraken.krakenhax;

import android.graphics.Bitmap;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;


/**
 * ViewModel for managing event data.
 * This class handles the business logic for fetching, creating, and updating events in Firestore.
 */
public class EventViewModel extends ViewModel {
    private static MutableLiveData<ArrayList<Event>> eventList;
    private final FirebaseFirestore db;
    private final CollectionReference eventCollection;
    private final StorageReference storageRef;

    /**
     * Constructor for EventViewModel.
     * Initializes Firestore, gets a reference to the "Events" collection, and sets up a real-time snapshot listener.
     */
    public EventViewModel() {
        eventList = new MutableLiveData<>(new ArrayList<>());
        // Initialize the Firestore database and get the "Events" collection reference.
        db = FirebaseFirestore.getInstance();
        eventCollection = db.collection("Events");
        storageRef = FirebaseStorage.getInstance().getReference();
        // Start listening for real-time updates from Firestore.
        addSnapshotListener();
    }

    /**
     * Adds an event to the local list and triggers an upload to Firestore.
     *
     * @param event The event to be added.
     */
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

    /**
     * Uploads a poster image for a given event to Firebase Storage.
     * On success, it updates the event document in Firestore with the new image URL.
     *
     * @param event    The event to which the poster belongs.
     * @param filePath The local Uri of the image file to be uploaded.
     */
    public void uploadPosterForEvent(Event event, Uri filePath) {
        if (filePath != null && event != null && event.getId() != null) {
            StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");
            UploadTask uploadTask = eventPosterRef.putFile(filePath);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);
                    event.setPoster(downloadUrl);
                    db.collection("Events")
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

    public Bitmap generateQR(String eventId) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        // The try-catch is now handled by the caller (QrCodeFragment), which is better practice.
        BitMatrix matrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 400, 400); // Increased size for clarity
        BarcodeEncoder encoder = new BarcodeEncoder();
        return encoder.createBitmap(matrix);
    }

    /**
     * Uploads an event object to the "Events" collection in Firestore.
     *
     * @param event The event object to be uploaded.
     */
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

    /**
     * Sets up a Firestore snapshot listener to get real-time updates for the events collection.
     * This keeps the local event list synchronized with the database.
     */
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