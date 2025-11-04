package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


// TODO: add the other details for myevents and display
// TODO: pass in event from MyEventsFragment
public class MyEventDetailsFragment extends Fragment {
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView imgPoster;
    private Button btnUploadPoster;
    private Button btnBack;
    private ActivityResultLauncher<String> imagePicker;
    private Uri filePath;

    //hardcoded event for now
    //private Event selectedEvent;

    public MyEventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_event_details, container, false);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = storage.getReference();

        imgPoster = view.findViewById(R.id.imgPoster);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        btnBack = view.findViewById(R.id.btnBack);

        // Hardcoded event
        /*
        selectedEvent = new Event();
        selectedEvent.setId("test_event_001");
        selectedEvent.setTitle("Swimming Lessons");
        selectedEvent.setEventDetails("Test upload event");
        selectedEvent.setPoster(null);
        */

        // Image picker
        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                try {
                    Picasso.get().load(uri).fit().centerInside().into(imgPoster);

                    uploadPosterForEvent();
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image", e);
                    new AlertDialog.Builder(requireContext()).setTitle("Error").setMessage("Failed to load image. Please try again.").setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
                }
            }
        });

        btnUploadPoster.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        return view;
    }

    public void uploadPosterForEvent() {
        if (filePath != null) {
            // TODO: change "test_event2" to selectedEvent.getID() when firestore setup with events
            StorageReference eventPosterRef = storageRef.child("event_posters/" + "test_event2" + ".jpg");

            UploadTask uploadTask = eventPosterRef.putFile(filePath);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);

                    // TODO: uncomment when firestore setup with events
                    //selectedEvent.setPoster(downloadUrl);
                    //db.collection("events")
                    //        .document(selectedEvent.getId()) // or event ID
                    //        .set(selectedEvent); // overwrites or creates the document

                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Upload failed", e);
            });
        }
    }
}