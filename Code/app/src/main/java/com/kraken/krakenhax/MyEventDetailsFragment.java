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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageRegistrar;
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
    private Button deleteButton;

    private Button btnentrantInfo;
    private ActivityResultLauncher<String> imagePicker;
    private Uri filePath;
    private Event event;

    //hardcoded event for now
    //private Event selectedEvent;

    public MyEventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_event_details, container, false);

        assert getArguments() != null;
        event = getArguments().getParcelable("event_id");

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = storage.getReference();

        imgPoster = view.findViewById(R.id.imgPoster);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        btnBack = view.findViewById(R.id.btnBack);
        btnentrantInfo = view.findViewById(R.id.btn_entrant_info);
        deleteButton = view.findViewById(R.id.DeleteButton);


        // Set the event location
        TextView tvLocation2 = view.findViewById(R.id.tv_location_field2);
        tvLocation2.setText(event.getLocation());

        // Hardcoded event
        /*
        selectedEvent = new Event();
        selectedEvent.setId("test_event_001");
        selectedEvent.setTitle("Swimming Lessons");
        selectedEvent.setEventDetails("Test upload event");
        selectedEvent.setPoster(null);
        */
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
        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        assert event != null;
        tvEventName.setText(event.getTitle());

        TextView tvDescription = view.findViewById(R.id.tv_event_description);
        tvDescription.setText(event.getEventDetails());




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

        btnBack.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        btnentrantInfo.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putParcelable("event", event);
            NavHostFragment.findNavController(this).navigate(R.id.action_MyEventDetailsFragment_to_EntrantInfoFragment, bundle);

        });

        deleteButton.setOnClickListener(v -> {
            db.collection("events").document(event.getId()).delete().addOnSuccessListener(aVoid -> {
            Log.d("Firebase", "Event deleted successfully");
            String posterUrl = event.getPoster();
            if (posterUrl != null && !posterUrl.isEmpty()) {
                StorageReference posterRef = storage.getReferenceFromUrl(posterUrl);
                posterRef.delete().addOnSuccessListener(aVoid1 -> {
                    Log.d("Firebase", "Poster deleted successfully");
                }).addOnFailureListener(e -> {
                    Log.e("Firebase", "Error deleting poster", e);
                });
            }
            NavHostFragment.findNavController(this).navigate(R.id.action_MyEventDetailsFragment_to_MyEventsFragment);
        });

        });
        return view;
    }

    public void uploadPosterForEvent() {
        if (filePath != null) {
            // TODO: change "test_event2" to selectedEvent.getID() when firestore setup with events
            StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");

            UploadTask uploadTask = eventPosterRef.putFile(filePath);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Get the download URL
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);
                    event.setPoster(downloadUrl);
                    // TODO: uncomment when firestore setup with events

                    db.collection("events")
                            .document(event.getId()) // or event ID
                            .set(event); // overwrites or creates the document

                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Upload failed", e);
            });
        }
    }
}