package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Fragment} that displays the details of a single event.
 * It handles user interactions such as signing up, withdrawing, accepting, declining, and deleting an event.
 * The UI dynamically changes based on the user's role and their status for the event.
 */
public class EventFragment extends Fragment {
    private Profile currentUser;
    private Button buttonSignup;
    private Button buttonAccept;
    private Button buttonDecline;
    private Button buttonNotify;
    private Button deleteButton;
    private FirebaseFirestore db;
    private Event event;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public EventFragment() {
        // Required empty public constructor
    }

    /**
     * Updates the given event document in the "Events" collection in Firestore.
     * @param event The event object to be updated.
     */
    private void updateEventInFirestore(Event event) {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event updated successfully!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event", e));
        }
    }

    /**
     * Deletes the given event document from the "Events" collection in Firestore.
     * On success, it navigates back to the previous screen.
     * @param event The event object to be deleted.
     * @param navController The NavController used to navigate back.
     */
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

    /**
     * Dynamically updates the visibility and text of action buttons based on the user's relationship with the event.
     * @param view The parent view containing the buttons.
     * @param event The event being displayed.
     * @param navController The NavController for navigation.
     */
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
        } else if (event.getWaitList().contains(currentUser)) {
            buttonSignup.setText("Withdraw");
            buttonSignup.setOnClickListener(v -> {
                event.removeFromWaitList(currentUser);
                updateEventInFirestore(event);
                updateButtons(view, event, navController);
            });
        } else {
            buttonSignup.setText("Sign Up");
            buttonSignup.setOnClickListener(v -> {
                event.addToWaitList(currentUser);
                updateEventInFirestore(event);
                updateButtons(view, event, navController);
            });
        }
    }

    /**
     * Inflates the user interface view for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned,
     * but before any saved state has been restored in to the view.
     * This is where UI components are initialized, event data is retrieved, and listeners are set up.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        Bundle args = getArguments();
        if (args != null && args.containsKey("event_name")) {
            event = args.getParcelable("event_name");
        }

        // From QR Code
        String eventId = null;
        if (args != null && args.containsKey("eventId")) {
            eventId = args.getString("eventId");
        }

        if (eventId != null && !eventId.isEmpty()) {
            db.collection("Events").document(eventId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            event = (Event) document.toObject(Event.class);
                        } else {
                            // Document does not exist
                            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                            navController.navigate(R.id.action_EventFragment_to_EventsFragment);
                        }
                    } else {
                        Log.d("EventFragment", "get failed with ", task.getException());
                        Toast.makeText(requireContext(), "Error retrieving event", Toast.LENGTH_SHORT).show();
                        navController.navigate(R.id.action_EventFragment_to_EventsFragment);
                    }
                }
            });
            return;
        }

        // No event info provided
        Toast.makeText(requireContext(), "No event data provided", Toast.LENGTH_SHORT).show();
        navController.navigate(R.id.action_EventFragment_to_EventsFragment);

        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        assert event != null;
        tvEventName.setText(event.getTitle());

        TextView tvLocation = view.findViewById(R.id.tv_location_field);
        tvLocation.setText(event.getLocation());

        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            if (currentUser.getType().equals("Admin")) {
                navController.navigate(R.id.action_MyEventDetailsFragment_to_AdminListFragment);
            } else {
                navController.navigate(R.id.action_EventFragment_to_EventsFragment);
            }
        });

        // Update buttons for current user state
        updateButtons(view, event, navController);

        // DEMO NOTIFICATION BUTTON
        Button demoNotifyBtn = view.findViewById(R.id.button_notify);
        demoNotifyBtn.setOnClickListener(v -> {
            Profile mockProfile = new Profile();
            mockProfile.setUsername("Amaan");
            mockProfile.setNotificationsEnabled(true);

            NotifyUser notifyUser = new NotifyUser(requireContext());
            notifyUser.sendNotification(mockProfile, "This is a demo notification from KrakenHax!");
        });

        // REAL ORGANIZER BROADCAST
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
