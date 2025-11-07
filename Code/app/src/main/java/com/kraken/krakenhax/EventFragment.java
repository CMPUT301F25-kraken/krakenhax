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


/**
 * The Event Page
 */
public class EventFragment extends Fragment {
    private Profile currentUser;
    private FirebaseFirestore db;

    public EventFragment() {
        // Required empty public constructor
    }

    private void updateButtons(View view, Event event, NavController navController) {
        Button buttonAccept = view.findViewById(R.id.button_accept);
        Button buttonDecline = view.findViewById(R.id.button_decline);
        Button buttonSignup = view.findViewById(R.id.button_signup);

        // Logic for buttons depending on users status in the event.
        // WON LOTTERY, WAITING TO ACCEPT
        if (event.getWonList().contains(currentUser)) {
            buttonSignup.setVisibility(View.GONE);
            buttonAccept.setVisibility(View.VISIBLE);
            buttonDecline.setVisibility(View.VISIBLE);

            buttonAccept.setOnClickListener(v -> {
                event.addToAcceptList(currentUser);
                updateButtons(view, event, navController);
            });

            buttonDecline.setOnClickListener(v -> {
                event.addToCancelList(currentUser);
                updateButtons(view, event, navController);
            });

            // WON LOTTERY, CANCELED ENTRY
        } else if (event.getCancelList().contains(currentUser)) {
            buttonSignup.setClickable(false);
            buttonSignup.setText("You cancelled your entry");

            // LOST LOTTERY
        } else if (event.getLostList().contains(currentUser)) {
            buttonSignup.setClickable(false);
            buttonSignup.setText("You were not selected");

            // ON WAITLIST
        } else if (event.getWaitList().contains(currentUser)) {
            buttonSignup.setText("Withdraw");

            buttonSignup.setOnClickListener(v -> {
                event.removeFromWaitList(currentUser);
                updateButtons(view, event, navController);
            });

            // NOT SIGNED UP
        } else {
            buttonSignup.setText("Sign Up");

            buttonSignup.setOnClickListener(v -> {
                event.addToWaitList(currentUser);
                updateButtons(view, event, navController);
            });

        }
    }

    private void populateEventView(View view, Event event, NavController navController) {
        if (event == null) {
            Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        tvEventName.setText(event.getTitle());

        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> navController.navigate(R.id.action_EventFragment_to_EventsFragment));

        Button buttonNotify = view.findViewById(R.id.button_notify);
        buttonNotify.setOnClickListener(v -> {
            NotifyUser notifyUser = new NotifyUser();
            notifyUser.sendNotification(currentUser, "Notification from organizer for " + event.getTitle());
        });

        updateButtons(view, event, navController);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        // Set up nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // FROM EventsFragment
        Event eventFromArgs = null;
        Bundle args = getArguments();
        if (args != null && args.containsKey("event_name")) {
            eventFromArgs = args.getParcelable("event_name");
        }

        if (eventFromArgs != null) {
            populateEventView(view, eventFromArgs, navController);
            return;
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
                            Event eventFromDb = document.toObject(Event.class);
                            populateEventView(view, eventFromDb, navController);
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
    }
}