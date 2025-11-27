package com.kraken.krakenhax;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


/**
 * The Event Page — displays event details and provides sign-up / cancel / notification functionality.
 */
public class EventFragment extends Fragment {
    private Profile currentUser;
    private FirebaseFirestore db;
    private ProfileViewModel profileModel;

    private ActivityResultLauncher<String[]> locationPermissionRequest;
    private NavController navController;
    private Event event;
    private TextView tvWaitlistEntry;
    private final android.os.Handler timerHandler = new android.os.Handler();
    private Runnable waitlistRunnable;
    private Runnable deadlineRunnable;
    private Runnable updateButtonRunnable;
    private StorageReference storageRef;
    private EventViewModel eventViewModel;


    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Updates an event in Firestore.
     */
    private void updateEventInFirestore() {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event updated successfully!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event", e));
        }
        DocumentReference profRef = db.collection("Profiles").document(currentUser.getID());
        profRef.set(currentUser);
        //profRef.update("myWaitlist", FieldValue.arrayUnion(event.getId()));
    }

    /**
     * Deletes an event from Firestore.
     */
    private void deleteEventFromFirestore() {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Event deleted successfully!");
                        navController.popBackStack(); // Go back after deleting
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting event", e));
        }
    }

    /**
     * Prompts the user to give permission to access their location.
     */
    private void requestLocationPermissions() {
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * Gets the entrants location and adds them to the waitlist for the event.
     */
    private void getLocationAndJoinWaitlist() {
        // Get view
        View view = getView();
        if (view == null) return;

        FusedLocationProviderClient fused = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        fused.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                // Save to profile
                currentUser.setLatitude(lat);
                currentUser.setLongitude(lng);

                FirebaseFirestore.getInstance()
                        .collection("Profiles")
                        .document(currentUser.getID())
                        .update("latitude", lat, "longitude", lng);

                Toast.makeText(requireContext(),
                        "Location saved: " + lat + ", " + lng,
                        Toast.LENGTH_SHORT).show();
                joinWaitlist();

            } else {
                Toast.makeText(requireContext(),
                        "Could not get location. Not added to WaitList Try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Adds the entrant to the waitlist for an event.
     */
    private void joinWaitlist() {
        // Get view
        View view = getView();
        if (view == null) return;
        //currentUser.addToMyWaitlist(event.getId());
        updateEventInFirestore();
        updateButtons();
    }

    /**
     * Updates the accept, decline, signup, and delete buttons for an event depending on the state.
     */
    private void updateButtons() {
        // Use a timer to update the state of the buttons live
        updateButtonRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if the view is no longer valid from the fragment being destroyed
                if (getView() == null) {
                    timerHandler.removeCallbacks(this);
                    return;
                }

                // Get view
                View view = getView();
                if (view == null) return;

                // Initialize buttons
                Button buttonAccept = view.findViewById(R.id.button_accept);
                Button buttonDecline = view.findViewById(R.id.button_decline);
                Button buttonSignup = view.findViewById(R.id.button_signup);
                Button deleteButton = view.findViewById(R.id.EventDeleteButton);

                if (currentUser.getType().equals("Admin")) {
                    buttonSignup.setVisibility(View.GONE);
                    deleteButton.setVisibility(View.VISIBLE);
                }

                if (event.getWonList().contains(currentUser)) {
                    buttonSignup.setVisibility(View.GONE);
                    buttonAccept.setVisibility(View.VISIBLE);
                    buttonDecline.setVisibility(View.VISIBLE);

                    buttonAccept.setOnClickListener(v -> {
                        event.addToAcceptList(currentUser);
                        updateEventInFirestore();
                        buttonAccept.setVisibility(View.GONE);
                        buttonDecline.setVisibility(View.GONE);
                        buttonSignup.setVisibility(View.VISIBLE);
                        updateButtons();
                    });

                    buttonDecline.setOnClickListener(v -> {
                        event.addToCancelList(currentUser);
                        updateEventInFirestore();
                        buttonAccept.setVisibility(View.GONE);
                        buttonDecline.setVisibility(View.GONE);
                        buttonSignup.setVisibility(View.VISIBLE);
                        updateButtons();
                    });

                } else if (event.getCancelList().contains(currentUser)) {
                    buttonSignup.setClickable(false);
                    buttonSignup.setText("You cancelled your entry");

                } else if (event.getAcceptList().contains(currentUser)) {
                    buttonSignup.setClickable(false);
                    buttonSignup.setText("You accepted your entry");

                } else if (event.getLostList().contains(currentUser)) {
                    buttonSignup.setClickable(false);
                    buttonSignup.setText("You were not selected");
                } else if (event.getWaitList().contains(currentUser)) {
                    buttonSignup.setText("Withdraw");
                    buttonSignup.setOnClickListener(v -> {
                        event.removeFromWaitList(currentUser);
                        //currentUser.removeFromMyWaitList(event.getId());
                        updateEventInFirestore();
                        updateButtons();

                        // Notify user
                        NotifyUser notifyUser = new NotifyUser(requireContext());
                        notifyUser.sendNotification(currentUser,
                                "❌ You have withdrawn from " + event.getTitle());
                    });
                    //event.getWaitList().contains(currentUser)
                } else {
                    buttonSignup.setText("Sign Up");
                    buttonSignup.setOnClickListener(v -> {
                        //event.addToWaitList(currentUser);
                        //currentUser.addToMyWaitlist(event.getId());
                        //updateEventInFirestore(event);
                        //updateButtons(view, event, navController);

                        if (event.getUseGeolocation()) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                // User denied once normally → you can ask again
                                new AlertDialog.Builder(requireContext())
                                        .setTitle("Location Required")
                                        .setMessage("We need your location to join this event. Please allow it.")
                                        .setPositiveButton("OK", (d, w) -> requestLocationPermissions())
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            } else {
                                // First time → ask directly
                                requestLocationPermissions();
                            }
                        } else {
                            joinWaitlist();
                        }
                        updateButtons();
                    });
                }

                // Update the waitlist info
                setWaitlistInfo();

                // Set the timer to repeat this code every 1 second
                timerHandler.postDelayed(this, 1000);
            }
        };

        // Start the timer
        timerHandler.post(updateButtonRunnable);
    }

    /**
     * Returns a location permission request object.
     */
    private ActivityResultLauncher<String[]> requestLocationPermission() {
        // Get view
        View view = getView();
        assert view != null;

        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean hasLocationPermission =
                            Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION)) || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));

                    if (hasLocationPermission) {
                        // User allowed at least one location permission
                        getLocationAndJoinWaitlist();
                    } else {
                        // Permission denied
                        boolean showRationaleFine =
                                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
                        boolean showRationaleCoarse =
                                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION);

                        if (!showRationaleFine && !showRationaleCoarse) {
                            // User denied location twice
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Location Permission Needed")
                                    .setMessage("To join events that require geolocation, please enable location " +
                                            "permission for this app in Settings.")
                                    .setPositiveButton("Open Settings", (dialog, which) -> {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        } else {
                            //
                            Toast.makeText(requireContext(),
                                    "This event needs location, please try again and allow location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                }

        );



        return locationPermissionRequest;
    }

    /**
     * Sets the count of how many people are on the waitlist.
     */
    private void setWaitlistInfo() {
        // Use a timer to update the text live
        waitlistRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if the view is no longer valid from the fragment being destroyed
                if (getView() == null) {
                    timerHandler.removeCallbacks(this);
                    return;
                }

                List<Profile> waitlist = event.getWaitList();
                int numWaitlist = waitlist.size();
                int maxWaitlist = event.getWaitListCap();
                if (maxWaitlist != 0) {
                    tvWaitlistEntry.setText(String.format("%d / %d", numWaitlist, maxWaitlist));
                } else {
                    tvWaitlistEntry.setText(String.format("%d / infinity", numWaitlist));
                }


                // Set the timer to repeat this code every 10 seconds
                timerHandler.postDelayed(this, 10000);

            }
        };

        // Start the timer
        timerHandler.post(waitlistRunnable);
    }

    /**
     * Sets the event poster.
     */
    private void setEventPoster(ImageView eventImage) {
        String posterURL = event.getPoster();
        if (posterURL == null || posterURL.isEmpty()) {
            eventImage.setImageResource(R.drawable.outline_attractions_100);
        } else {
            Picasso.get()
                    .load(posterURL)
                    .placeholder(R.drawable.outline_attractions_100)
                    .error(R.drawable.outline_attractions_100)
                    .fit().centerCrop()
                    .into(eventImage);
        }
    }

    /**
     * Sets the organizer button to display the name of the organizer and navigate to the organizers
     * page when pressed.
     */
    private void setOrganizerButton(Button buttonEventOrganizer) {
        String organizerID = event.getOrgId();

        profileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
            for (Profile profile : profiles) {
                if (profile.getID().equals(organizerID)) {
                    String organizerName = profile.getUsername();
                    buttonEventOrganizer.setText(organizerName);

                    // Go to the page with all the events by that organizer
                    buttonEventOrganizer.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("organizer", profile);
                        navController.navigate(R.id.action_EventFragment_to_OrganizerFragment, bundle);
                    });
                    break;
                }
            }
        });
    }

    /**
     * Displays a countdown of the time remaining until the registration deadline for an event closes.
     */
    private void setRegistrationDeadline(TextView tvRegistrationInfo) {
        // Get view
        View view = getView();
        if (view == null) return;

        // Use runnable to update the textview live
        deadlineRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if the view is no longer valid from the fragment being destroyed
                if (getView() == null) {
                    timerHandler.removeCallbacks(this);
                    return;
                }

                try {
                    List<Timestamp> timeframe = event.getTimeframe();
                    Timestamp deadline = timeframe.get(1);
                    Timestamp currentTime = Timestamp.now();

                    long timeRemaining = deadline.toDate().getTime() - currentTime.toDate().getTime();

                    // If deadline has passed
                    if (timeRemaining <= 0) {
                        tvRegistrationInfo.setText("Registration has closed.");
                        Button signupButton = view.findViewById(R.id.button_signup);
                        signupButton.setVisibility(View.GONE);
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

                        // Set the timer to repeat this code every 10 seconds
                        timerHandler.postDelayed(this, 10000);
                    }
                    // Throw an exception when the timeframe array for the event is empty
                } catch (IndexOutOfBoundsException e) {
                    tvRegistrationInfo.setText("ERROR: This event has an invalid or empty timeframe.");
                    //throw new IllegalStateException("The event titled '" + event.getTitle() + "' (ID: " + event.getId() + ") has an invalid or empty timeframe. It must contain at least two timestamps.", e);
                }
            }
        };

        // Start the timer
        timerHandler.post(deadlineRunnable);
    }

    /**
     * Sets the datetime field to display the date and time an event will take place.
     */
    private void setEventDate(TextView tvDateTime) {
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
     * Set up a Firestore snapshot listener to get real-time updates for the event.
     */
    private void startFirestoreListener() {
        if (event == null || event.getId() == null) {
            return;
        }

        db.collection("Events").document(event.getId())
                .addSnapshotListener((snapshot, e) -> {
                    assert snapshot != null;
                    Event updatedEvent = snapshot.toObject(Event.class);

                    if (updatedEvent != null) {
                        this.event = updatedEvent;

                        // Update the UI for the buttons
                        updateButtons();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the object for the event
        assert getArguments() != null;
        event = getArguments().getParcelable("event");
        assert event != null;
        ImageView qrImageView = view.findViewById(R.id.qr_imageview);
        // Set up the nav controller
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        if (event.getQrCodeURL() == null) {
            Log.e("EventFragment", "QR image URL is null");
        } else {
            eventViewModel.urlToBitmap(getActivity().getApplicationContext(), event.getQrCodeURL());
        }
        Log.d("QRDEBUG", "URL value = '" + event.getQrCodeURL() + "'");

        eventViewModel.getDownloadedBitmap().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                qrImageView.setImageBitmap(bitmap);
            } else {
                qrImageView.setImageResource(R.drawable.outline_beach_access_100);
            }
        });



        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();


        // Start a firestore listener for the event
        startFirestoreListener();

        // Get the object for the current user
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        // Creates an instance of the ProfileViewModel
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Location permission
        locationPermissionRequest = requestLocationPermission();

        // Set the event name
        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        tvEventName.setText(event.getTitle());

        // Set the event location
        TextView tvLocation = view.findViewById(R.id.tv_location_field);
        tvLocation.setText(event.getLocation());

        // Set the event description
        TextView tvDescription = view.findViewById(R.id.tv_event_description);
        tvDescription.setText(event.getEventDetails());

        // Set up the waitlist info
        tvWaitlistEntry = view.findViewById(R.id.tv_waitlist_entry);
        setWaitlistInfo();

        // Set the event poster
        ImageView eventImage = view.findViewById(R.id.event_image);
        setEventPoster(eventImage);

        Button photoDelete = view.findViewById(R.id.delete_event_Photo);
        if (currentUser.getType().equals("Admin")){
            photoDelete.setVisibility(View.VISIBLE);
        } else {
            photoDelete.setVisibility(View.GONE);
        }
        photoDelete.setOnClickListener(v -> {

            event.setPoster(null);
            deleteEventPic();
            setEventPoster(eventImage);
            updateEventInFirestore();
        });




        // Set up the back button
        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> navController.popBackStack());

        // Set the view event organizer button to show the name of the organizer and navigate to the organizers page
        Button buttonEventOrganizer = view.findViewById(R.id.button_event_organizer);
        setOrganizerButton(buttonEventOrganizer);

        // Set tvRegistrationInfo to display the deadline for registration
        TextView tvRegistrationInfo = view.findViewById(R.id.tv_registration_info);
        setRegistrationDeadline(tvRegistrationInfo);

        // Set the tvDateTime to show the date and time of the event
        TextView tvDateTime = view.findViewById(R.id.tv_date_time);
        setEventDate(tvDateTime);

        // Update buttons for current user state
        updateButtons();

        // Delete button logic
        Button deleteButton = view.findViewById(R.id.EventDeleteButton);
        if (currentUser.getType().equals("Admin") || currentUser.getType().equals("Organizer")) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }
        deleteButton.setOnClickListener(v -> {
            deleteEventFromFirestore();
            navController.popBackStack();
        });

    }
    public void deleteEventPic() {
        StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");
        eventPosterRef.delete().addOnSuccessListener(aVoid -> {
            // Profile picture deleted successfully
            Log.d("EventFragment", "Event poster deleted successfully");
        }).addOnFailureListener(e -> {
            // Error
            Log.e("Firebase", "Delete event poster failed", e);
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the timer to prevent memory leaks or crashing
        if (timerHandler != null) {
            timerHandler.removeCallbacks(waitlistRunnable);
            timerHandler.removeCallbacks(deadlineRunnable);
            timerHandler.removeCallbacks(updateButtonRunnable);
        }
    }

}
