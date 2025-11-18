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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/**
 * A {@link Fragment} that displays the detailed view of an event created by the current user (organizer).
 * It allows the organizer to upload a poster, view entrant information, and displays event details in real-time.
 */
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
    //private Profile currentUser;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public MyEventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment, initializes Firestore and Storage,
     * and sets up listeners for UI components.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_event_details, container, false);

        assert getArguments() != null;
        // Get the initial (potentially stale) event object from the arguments
        event = getArguments().getParcelable("event_name");

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = storage.getReference();

        imgPoster = view.findViewById(R.id.imgPoster);
        btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        btnBack = view.findViewById(R.id.btnBack);
        btnentrantInfo = view.findViewById(R.id.btn_entrant_info);
//
//         MainActivity mainActivity = (MainActivity) getActivity();
//         assert mainActivity != null;
//         currentUser = mainActivity.currentUser;
//
//         if (Objects.equals(currentUser.getType(), "Entrant")) {
//         btnentrantInfo.setVisibility(View.GONE);
//         btnUploadPoster.setVisibility(View.GONE);
//         }
//
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

    /**
     * Sets up a real-time Firestore snapshot listener for the current event document.
     * This ensures the UI is always displaying the most up-to-date event information.
     *
     * @param view The root view of the fragment, used to find UI elements to update.
     */
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

    /**
     * Updates all UI elements in the fragment with the latest data from the event object.
     *
     * @param view The root view of the fragment, used to find UI elements.
     */
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

        // Set up the waitlist info
        TextView tvWaitlistEntry = view.findViewById(R.id.tv_waitlist_entry);
        List<Profile> waitlist = event.getWaitList();
        int numWaitlist = waitlist.size();
        int maxWaitlist = event.getWaitListCap();
        if (maxWaitlist != 0) {
            tvWaitlistEntry.setText(String.format("%d / %d", numWaitlist, maxWaitlist));
        } else {
            tvWaitlistEntry.setText(String.format("%d / infinity", numWaitlist));
        }

        // Set tvRegistrationInfo to display the deadline for registration
        TextView tvRegistrationInfo = view.findViewById(R.id.tv_registration_info);
        try {
            List<Timestamp> timeframe = event.getTimeframe();
            Timestamp deadline = timeframe.get(1);
            Timestamp currentTime = Timestamp.now();

            long timeRemaining = deadline.toDate().getTime() - currentTime.toDate().getTime();

            // If deadline has passed
            if (timeRemaining <= 0) {
                tvRegistrationInfo.setText("Registration has closed.");
            } else {
                long days = timeRemaining / (1000 * 60 * 60 * 24);
                long hours = (timeRemaining / (1000 * 60 * 60)) % 24;
                long minutes = (timeRemaining / (1000 * 60)) % 60;

                // Create a string with time remaining nicely formatted
                StringBuilder remainingText = new StringBuilder("Time remaining: ");
                if (days > 0) {
                    remainingText.append(days).append(days == 1 ? " day" : " days");
                }
                if (hours > 0) {
                    if (days > 0) remainingText.append(", ");
                    remainingText.append(hours).append(hours == 1 ? " hour" : " hours");
                }
                if (minutes > 0) {
                    if (days > 0 || hours > 0) remainingText.append(", ");
                    remainingText.append(minutes).append(minutes == 1 ? " minute" : " minutes");
                }

                tvRegistrationInfo.setText(remainingText.toString());
            }
            // Throw an exception when the timeframe array for the event is empty
        } catch (IndexOutOfBoundsException e) {
            tvRegistrationInfo.setText("ERROR: This event has an invalid or empty timeframe.");
            //throw new IllegalStateException("The event titled '" + event.getTitle() + "' (ID: " + event.getId() + ") has an invalid or empty timeframe. It must contain at least two timestamps.", e);
        }

        // Set the tvDateTime to show the date and time of the event
        TextView tvDateTime = view.findViewById(R.id.tv_date_time);
        if (event.getDateTime() != null) {
            Timestamp dateTime = event.getDateTime();
            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy, hh:mm a", Locale.getDefault());
            String formattedDateTime = formatter.format(dateTime.toDate());
            tvDateTime.setText(formattedDateTime);
        } else {
            tvDateTime.setText("ERROR: This event is missing a date and time");
        }
    }

    /**
     * Uploads the selected poster image to Firebase Storage and updates the event document with the image URL.
     */
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
