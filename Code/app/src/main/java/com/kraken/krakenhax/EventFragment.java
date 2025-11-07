package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * The Event Page
 */
public class EventFragment extends Fragment {
    private Profile currentUser;
    private Button buttonSignup;
    private Button buttonAccept;
    private Button buttonDecline;
    private Button buttonNotify;
    private Button deleteButton;


    public EventFragment() {
        // Required empty public constructor
    }

    private void updateButtons(View view, Event event, NavController navController) {
        buttonAccept = view.findViewById(R.id.button_accept);
        buttonDecline = view.findViewById(R.id.button_decline);
        buttonSignup = view.findViewById(R.id.button_signup);
        deleteButton = view.findViewById(R.id.EventDeleteButton);

        // Logic for buttons depending on users status in the event.
        // WON LOTTERY, WAITING TO ACCEPT
        if (currentUser.getType().equals("Admin")){
            buttonSignup.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
            //buttonNotify.setVisibility(View.VISIBLE);
        }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        // Get the event object passed from the other fragment
        assert getArguments() != null;
        Event event = getArguments().getParcelable("event_name");

        // Set up nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set the textview to display the correct event name
        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        assert event != null;
        tvEventName.setText(event.getTitle());

        // Set the event location
        TextView tvLocation = view.findViewById(R.id.tv_location_field);
        tvLocation.setText(event.getLocation());

        // Set up on click listener for button to go back to events view
        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            if (currentUser.getType().equals("Admin")) {
                navController.navigate(R.id.action_MyEventDetailsFragment_to_AdminListFragment);
            } else {
                navController.navigate(R.id.action_EventFragment_to_EventsFragment);

            }
        });

        updateButtons(view, event, navController);

        // Demo: simulate organizer notification for this event
        buttonNotify = view.findViewById(R.id.button_notify);
        if (currentUser.getType().equals("Admin")) {
            buttonNotify.setVisibility(View.GONE);
        }
        buttonNotify.setOnClickListener(v -> {
            NotifyUser notifyUser = new NotifyUser();
            notifyUser.sendNotification(new Profile("1","DemoUser", "pass", "Entrant", "demo@example.com", "0"),
                    "Notification from organizer for " + event.getTitle());
        });

    }
}