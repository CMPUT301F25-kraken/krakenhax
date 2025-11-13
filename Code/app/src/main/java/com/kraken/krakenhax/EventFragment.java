package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * The Event Page — displays event details and provides sign-up / cancel / notification functionality.
 */
public class EventFragment extends Fragment {
    private Profile currentUser;
    private Button buttonSignup;
    private Button buttonAccept;
    private Button buttonDecline;
    private Button buttonNotify;
    private Button deleteButton;
    private FirebaseFirestore db;

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

    private void updateButtons(View view, Event event, NavController navController) {
        buttonAccept = view.findViewById(R.id.button_accept);
        buttonDecline = view.findViewById(R.id.button_decline);
        buttonSignup = view.findViewById(R.id.button_signup);
        deleteButton = view.findViewById(R.id.EventDeleteButton);

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
                event.addToWaitList(currentUser);
                currentUser.addToMyWaitlist(event.getId());
                updateEventInFirestore(event);
                updateButtons(view, event, navController);

                // Notify user
                NotifyUser notifyUser = new NotifyUser(requireContext());
                notifyUser.sendNotification(currentUser,
                        "✅ You have successfully signed up for " + event.getTitle());
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();

        // Get the object for the current user
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        // Get the object for the event
        assert getArguments() != null;
        Event event = getArguments().getParcelable("event_name");

        // Set up the nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

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

        // Set the event poster
        ShapeableImageView eventImage = view.findViewById(R.id.event_image);
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
        buttonBack.setOnClickListener(v -> {
            if (currentUser.getType().equals("Admin")) {
                navController.navigate(R.id.action_MyEventDetailsFragment_to_AdminListFragment);
            } else {
                navController.navigate(R.id.action_EventFragment_to_EventsFragment);
            }
        });

        // Set the view event organizer button to show the name of the organizer
        String organizerID = event.getOrgId();
        ProfileViewModel profileViewModel = new ProfileViewModel();
        Button buttonEventOrganizer = view.findViewById(R.id.button_event_organizer);
        ProfileViewModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
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
        buttonNotify = view.findViewById(R.id.button_notify);
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
        deleteButton = view.findViewById(R.id.EventDeleteButton);
        deleteButton.setOnClickListener(v -> deleteEventFromFirestore(event, navController));
    }

}
