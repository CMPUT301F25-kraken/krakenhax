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
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MyEventDetailsFragment extends Fragment {
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView imgPoster;
    private Button btnUploadPoster;
    private Button btnBack;

    private Button btnentrantInfo;
    private ActivityResultLauncher<String> imagePicker;
    private Uri filePath;
    private Event event;

    public MyEventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_event_details, container, false);

        assert getArguments() != null;
        // Get the initial (potentially stale) event object from the arguments
        event = getArguments().getParcelable("event_id");

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = storage.getReference();

        imgPoster = view.findViewById(R.id.imgPoster);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        btnBack = view.findViewById(R.id.btnBack);
        btnentrantInfo = view.findViewById(R.id.btn_entrant_info);

        // Set up a real-time listener for the event
        setupFirestoreListener(view);

        // Image picker
        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(imgPoster);
                    uploadPosterForEvent();
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image", e);
                    new AlertDialog.Builder(requireContext()).setTitle("Error").setMessage("Failed to load image. Please try again.").setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
                }
            }
        });

        btnUploadPoster.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        btnentrantInfo.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            // Pass the most up-to-date event object
            bundle.putParcelable("event", event);
            NavHostFragment.findNavController(this).navigate(R.id.action_MyEventDetailsFragment_to_EntrantInfoFragment, bundle);
        });

        return view;
    }

    private void setupFirestoreListener(View view) {
        if (event == null || event.getId() == null) {
            Log.e("Firestore", "Event or Event ID is null. Cannot set up listener.");
            return;
        }

        final DocumentReference docRef = db.collection("Events").document(event.getId());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("Firestore", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("Firestore", "Current data: " + snapshot.getData());
                // Update the local event object with the latest data
                event = snapshot.toObject(Event.class);
                // Update the UI with the new data
                updateUI(view);
            } else {
                Log.d("Firestore", "Current data: null");
            }
        });
    }

    private void updateUI(View view) {
        if (event == null) return;

        // Find views
        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        TextView tvDescription = view.findViewById(R.id.tv_event_description);
        TextView tvLocation2 = view.findViewById(R.id.tv_location_field2);

        // Set text
        tvEventName.setText(event.getTitle());
        tvDescription.setText(event.getEventDetails());
        tvLocation2.setText(event.getLocation());

        // Load poster image
        String poster = event.getPoster();
        if (poster == null || poster.isEmpty()) {
            imgPoster.setImageResource(R.drawable.outline_attractions_100);
        } else {
            Picasso.get()
                    .load(poster)
                    .placeholder(R.drawable.outline_attractions_100)
                    .error(R.drawable.outline_attractions_100)
                    .fit().centerCrop()
                    .into(imgPoster);
        }
    }

    public void uploadPosterForEvent() {
        if (filePath != null && event != null && event.getId() != null) {
            StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");

            UploadTask uploadTask = eventPosterRef.putFile(filePath);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);
                    event.setPoster(downloadUrl);
                    // Corrected the collection name to "Events" (capital 'E')
                    db.collection("Events")
                            .document(event.getId())
                            .set(event);
                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Upload failed", e);
            });
        }
    }
}
