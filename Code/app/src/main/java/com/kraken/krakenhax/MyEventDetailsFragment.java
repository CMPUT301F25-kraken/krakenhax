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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


/**
 * A {@link Fragment} that displays the detailed view of an event created by the current user (organizer).
 * It allows the organizer to upload a poster, view entrant information, and displays event details in real-time.
 */
public class MyEventDetailsFragment extends Fragment {
    private Profile currentUser;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView imgPoster;
    private ActivityResultLauncher<String> imagePicker;
    private Uri filePath;
    private Event event;
    private EventViewModel eventViewModel;
    private ImageView qrCodeImage;
    private CollectionReference eventRef;
    private CollectionReference profileRef;

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
        assert getArguments() != null;
        event = getArguments().getParcelable("event");

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = storage.getReference();

        imgPoster = view.findViewById(R.id.imgPoster);
        Button btnUploadPoster = view.findViewById(R.id.btnUploadPoster);
        Button btnBack = view.findViewById(R.id.btnBack);
        Button btnentrantInfo = view.findViewById(R.id.btn_entrant_info);
        Button btnLottery = view.findViewById(R.id.btnLottery);
        Button saveQrButton = view.findViewById(R.id.save_qr_code_button);


        qrCodeImage = view.findViewById(R.id.qr_code_imageview);
        eventViewModel = new EventViewModel();


        // Set up delete profile pic button
        Button deleteButton = view.findViewById(R.id.btnDelete);
        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure you want to delete your event?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes, I want to evnt my account.",
                    (dialog, which) -> {
                        deleteEvent();
                        Log.d("MyEventDetailsFragment", "Event deleted");
                        dialog.cancel();
                        NavHostFragment.findNavController(this).navigateUp();
                    });

            builder.setNegativeButton(
                    "No!",
                    (dialog, which) ->
                            dialog.cancel()
            );

            AlertDialog alert = builder.create();
            alert.show();
        });

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

        btnLottery.setOnClickListener(v -> {
            if (event.getLostList().isEmpty()) {
                event.drawLottery(event.getWaitList(), event.getWinnerNumber());
            } else {
                event.drawLottery(
                        event.getLostList(),
                        event.getWinnerNumber() - event.getWonList().size()
                );
            }
            updateEventInFirestore(event);

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            for (Profile p : event.getWonList()) {
                if (p == null) continue;
                if (!p.isNotificationsEnabled()) continue;

                Notification notif = new Notification(
                        "You won the lottery!",
                        "You have been selected to participate in " + event.getTitle() + ".",
                        currentUser.getUsername(),          // sender = organizer
                        null,                      // timestamp will be set server-side
                        event.getId(),             // event ID
                        p.getUsername(),                 // recipient profile ID
                        false                      // read = false
                );

                db.collection("Profiles")
                        .document(p.getID())
                        .collection("Notifications")
                        .add(notif)
                        .addOnSuccessListener(docRef ->
                                docRef.update("timestamp", FieldValue.serverTimestamp())
                        );
            }

            // Notify losers: "You were not selected"
            for (Profile p : event.getLostList()) {
                if (p == null) continue;
                if (!p.isNotificationsEnabled()) continue;

                Notification notif = new Notification(
                        "Lottery result",
                        "Unfortunately, you were not selected for " + event.getTitle() + ".",
                        currentUser.getUsername(),
                        null,
                        event.getId(),
                        p.getUsername(),
                        false
                );

                db.collection("Profiles")
                        .document(p.getID())
                        .collection("Notifications")
                        .add(notif)
                        .addOnSuccessListener(docRef ->
                                docRef.update("timestamp", FieldValue.serverTimestamp())
                        );
            }

            // 4. UI feedback
            Toast.makeText(requireContext(),
                    "Lottery drawn successfully!",
                    Toast.LENGTH_SHORT
            ).show();
        });

        btnUploadPoster.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnBack.setOnClickListener(v -> NavHostFragment.findNavController(this).navigateUp());

        btnentrantInfo.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            // Pass the most up-to-date event object
            bundle.putParcelable("event", event);
            NavHostFragment.findNavController(this).navigate(R.id.action_MyEventDetailsFragment_to_EntrantInfoFragment, bundle);
        });

        saveQrButton.setOnClickListener(v -> {
            eventViewModel.saveImage(requireContext(), qrCodeImage);
            Toast.makeText(requireContext(), "QR code saved to gallery", Toast.LENGTH_SHORT).show();
            Log.d("ImageSave", "QR code saved to gallery");
            saveQrButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray));
        });

        return view;
    }

    /**
     * Deletes the current event and cleans up related data.
     * This removes the event document, and the event from all users waitlists.
     */
    public void deleteEvent() {
        if (Objects.equals(currentUser.getID(), event.getOrgId())) {
            String eventId = event.getId();

            // Delete the profile document from Firestore
            eventRef = db.collection("Events");
            eventRef.document(eventId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Event with id: " + event.getId() + " successfully deleted.");
                        Toast.makeText(requireContext(), "Event deleted", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(aVoid ->
                            Log.d("Firebase", "Delete event with id: " + event.getId() + " failed.")
                    );
        } else {
            Log.e("Firebase", "Current user is not the organizer. Cannot delete event.");
        }
    }

    /**
     * Called immediately after the fragment's view has been created.
     * Initializes the {@link EventViewModel}, loads the event QR code image,
     * and observes bitmap updates to display the QR code or a fallback image.
     *
     * @param view               The view returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        eventViewModel.clearDownloadedBitmap();

        String url = event.getQrCodeURL();

        if (url == null || url.trim().isEmpty() || url.equalsIgnoreCase("null")) {
            qrCodeImage.setImageResource(R.drawable.outline_beach_access_100);
        } else {
            eventViewModel.urlToBitmap(requireContext(), url);
        }
        Log.e("QRCODEDEBUG", "URL value = " + url);

        eventViewModel.getDownloadedBitmap().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                qrCodeImage.setImageBitmap(bitmap);
            } else {
                qrCodeImage.setImageResource(R.drawable.outline_beach_access_100);
            }
        });
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
                Log.d("QRDEBUG", "Loaded qrCodeURL = " + event.getQrCodeURL());
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
        TextView categoriesTexview = view.findViewById(R.id.categories_my_event_textview);

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
        // Set the categories text
        if (event.getCategories() != null && !event.getCategories().isEmpty()) {
            StringBuilder categoriesText = new StringBuilder("Categories: ");
            for (String category : event.getCategories()) {
                categoriesText.append(category).append(", ");
            }
            categoriesText.delete(categoriesText.length() - 2, categoriesText.length());
            categoriesTexview.setText(categoriesText.toString());
        }

    }

    /**
     * Uploads the selected poster image to Firebase Storage and updates the event document with the image URL.
     */
    public void uploadPosterForEvent() {
        if (filePath != null && event != null && event.getId() != null) {
            StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");

            UploadTask uploadTask = eventPosterRef.putFile(filePath);

            uploadTask.addOnSuccessListener(taskSnapshot ->
                    eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        Log.d("Firebase", "Download URL: " + downloadUrl);
                        event.setPoster(downloadUrl);
                        // Corrected the collection name to "Events" (capital 'E')
                        db.collection("Events")
                                .document(event.getId())
                                .set(event);
                    })
            ).addOnFailureListener(e ->
                    Log.e("Firebase", "Upload failed", e)
            );
        }
    }

    /**
     * Updates the given event in the Firestore "Events" collection.
     * If the event or its ID is null, the operation is skipped.
     * Logs the result of the update operation.
     *
     * @param event The event object containing the latest data to be saved.
     */
    private void updateEventInFirestore(Event event) {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event updated successfully!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event", e));
        }
    }

}
