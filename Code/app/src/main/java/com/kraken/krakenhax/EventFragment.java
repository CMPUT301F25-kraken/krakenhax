package com.kraken.krakenhax;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.location.Location;
import android.widget.Toast;


/**
 * The Event Page — displays event details and provides sign-up / cancel / notification functionality.
 */
public class EventFragment extends Fragment {
    private Profile currentUser;
    private FirebaseFirestore db;
    private ProfileViewModel profileModel;

    private ActivityResultLauncher<String[]> locationPermissionRequest;


    public EventFragment() {
        // Required empty public constructor
    }

    private void updateEventInFirestore(Event event) {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event updated successfully!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event", e));
        }
        DocumentReference profRef = db.collection("Profiles").document(currentUser.getID());
        profRef.set(currentUser);
        //profRef.update("myWaitlist", FieldValue.arrayUnion(event.getId()));
    }

    private void deleteEventFromFirestore(Event event, NavController navController) {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Event deleted successfully!");
                        navController.popBackStack(); // Go back after deleting
                    })
                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting event", e));
        }
    }
    private void requestLocationPermissions() {
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
    private void getLocationAndJoinWaitlist(View view, Event event, NavController navController) {
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
                joinWaitlist(view,event,navController);

            } else {
                Toast.makeText(requireContext(),
                        "Could not get location. Not added to WaitList Try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtons(View view, Event event, NavController navController) {
        Button buttonAccept = view.findViewById(R.id.button_accept);
        Button buttonDecline = view.findViewById(R.id.button_decline);
        Button buttonSignup = view.findViewById(R.id.button_signup);
        Button deleteButton = view.findViewById(R.id.EventDeleteButton);

        if (currentUser.getType().equals("Admin")) {
            buttonSignup.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
        } else if (event.getWonList().contains(currentUser)) {
            buttonSignup.setVisibility(View.GONE);
            buttonAccept.setVisibility(View.VISIBLE);
            buttonDecline.setVisibility(View.VISIBLE);

            buttonAccept.setOnClickListener(v -> {
                event.addToAcceptList(currentUser);
                updateEventInFirestore(event);
                updateButtons(view, event, navController);
            });

            buttonDecline.setOnClickListener(v -> {
                event.addToCancelList(currentUser);
                updateEventInFirestore(event);
                updateButtons(view, event, navController);
            });
        } else if (event.getCancelList().contains(currentUser)) {
            buttonSignup.setClickable(false);
            buttonSignup.setText("You cancelled your entry");
        } else if (event.getLostList().contains(currentUser)) {
            buttonSignup.setClickable(false);
            buttonSignup.setText("You were not selected");
        } else if (currentUser.getMyWaitlist().contains(event.getId())) {
            buttonSignup.setText("Withdraw");
            buttonSignup.setOnClickListener(v -> {
                event.removeFromWaitList(currentUser);
                currentUser.removeFromMyWaitList(event.getId());
                updateEventInFirestore(event);
                updateButtons(view, event, navController);

                // Notify user
                NotifyUser notifyUser = new NotifyUser(requireContext());
                notifyUser.sendNotification(currentUser,
                        "❌ You have withdrawn from " + event.getTitle());
            });
            //event.getWaitList().contains(currentUser)
        } else {
            buttonSignup.setText("Sign Up");
            buttonSignup.setOnClickListener(v -> {

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
                    joinWaitlist(view, event, navController);
                }

            });

        }
    }
    private void joinWaitlist(View view, Event event, NavController navController) {
        event.addToWaitList(currentUser);
        currentUser.addToMyWaitlist(event.getId());
        updateEventInFirestore(event);
        updateButtons(view, event, navController);
        // Notify user
        NotifyUser notifyUser = new NotifyUser(requireContext());
        notifyUser.sendNotification(currentUser,
                "✅ You have successfully signed up for " + event.getTitle());
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
        Event event = getArguments().getParcelable("event_name");

        // Set up the nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();

        // Get the object for the current user
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);


        // Location permission
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean hasLocationPermission =
                            Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION)) || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));

                    if (hasLocationPermission) {
                        // User allowed at least one location permission
                        getLocationAndJoinWaitlist(view, event, navController);
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


        // Set the event name
        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        assert event != null;
        tvEventName.setText(event.getTitle());

        // Set the event location
        TextView tvLocation = view.findViewById(R.id.tv_location_field);
        tvLocation.setText(event.getLocation());

        // Set the event description
        TextView tvDescription = view.findViewById(R.id.tv_event_description);
        tvDescription.setText(event.getEventDetails());

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

        // Set the event poster
        ImageView eventImage = view.findViewById(R.id.event_image);
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

        // Set up the back button
        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> navController.popBackStack());

        // Set the view event organizer button to show the name of the organizer and navigate to the organizers page
        String organizerID = event.getOrgId();
        Button buttonEventOrganizer = view.findViewById(R.id.button_event_organizer);

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

//        String organizerID = event.getOrgId();
//        // Get the profile for the organizer matching that id from firestore
//        ProfileViewModel profileViewModel = new ProfileViewModel();
//        LiveData<ArrayList<Profile>> profileList = profileViewModel.getProfileList();
//        Log.d("EventFragment", profileList.toString());
//        Button buttonEventOrganizer = view.findViewById(R.id.button_event_organizer);
//        buttonEventOrganizer.setText(profileList.toString());

        // Update buttons for current user state
        updateButtons(view, event, navController);

        // 🔔 DEMO NOTIFICATION BUTTON
        Button demoNotifyBtn = view.findViewById(R.id.button_notify);
        demoNotifyBtn.setOnClickListener(v -> {
            Profile mockProfile = new Profile();
            mockProfile.setUsername("Amaan");
            mockProfile.setNotificationsEnabled(true);

            NotifyUser notifyUser = new NotifyUser(requireContext());
            notifyUser.sendNotification(mockProfile, "This is a demo notification from KrakenHax!");
        });

        // 🔔 REAL ORGANIZER BROADCAST
        Button buttonNotify = view.findViewById(R.id.button_notify);
        if (currentUser.getType().equals("Admin")) {
            buttonNotify.setVisibility(View.GONE);
        }
        buttonNotify.setOnClickListener(v -> {
            NotifyUser notifyUser = new NotifyUser(requireContext());
            List<Profile> allUsers = new ArrayList<>();
            allUsers.addAll(event.getWaitList());
            allUsers.addAll(event.getWonList());
            allUsers.addAll(event.getLostList());
            allUsers.addAll(event.getCancelList());

            notifyUser.sendBroadcast(allUsers, "📢 Update: " + event.getTitle() + " has new updates!");
        });

        // Delete button logic
        Button deleteButton = view.findViewById(R.id.EventDeleteButton);
        deleteButton.setOnClickListener(v -> deleteEventFromFirestore(event, navController));
    }

}
