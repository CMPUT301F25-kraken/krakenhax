package com.kraken.krakenhax;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.time.LocalDateTime;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


/**
 * A fragment that displays lists of entrants for a specific event.
 * It allows viewing entrants who are waitlisted, enrolled, or have cancelled.
 */
public class EntrantInfoFragment extends Fragment {
    private final Handler timerHandler = new Handler();
    private Event event;
    public ProfileViewModel ProfileModel;
    private TextView entrantType;
    private RecyclerView profileRecycler;
    private Spinner spinner_list;
    private FirebaseFirestore db;
    private Runnable entrantListRunnable;
    private View notifyOverlay;
    private Profile currentUser;
    private NotificationJ notif;

    public EntrantInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the recycler view to display the selected event list.
     */
    private void updateRecyclerList(String status) {
        // STOP ANY PREVIOUS TIMER BEFORE STARTING A NEW ONE
        if (entrantListRunnable != null) {
            timerHandler.removeCallbacks(entrantListRunnable);
        }

        // Use a timer to update the info live
        entrantListRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if the view is no longer valid from the fragment being destroyed
                if (getView() == null) {
                    timerHandler.removeCallbacks(this);
                    return;
                }

                entrantType.setText("Entrant " + status);

                // Retrieve the selected list from the event
                ArrayList<Profile> targetList;
                switch (status) {
                    case "Waitlisted":
                        targetList = event.getWaitList();
                        break;
                    case "Won":
                        targetList = event.getWonList();
                        break;
                    case "Lost":
                        targetList = event.getLostList();
                        break;
                    case "Accepted":
                        targetList = event.getAcceptList();
                        break;
                    case "Cancelled":
                        targetList = event.getCancelList();
                        break;
                    default:
                        targetList = new ArrayList<>();
                        break;
                }

                ProfileAdapter adapter = new ProfileAdapter(targetList);

                // Set the listener for the remove button
                adapter.setOnRemoveClickListener(position -> {
                    //Profile profileToRemove = targetList.get(position);

                    // Remove the user from the target list
                    Profile user = targetList.get(position);
                    targetList.remove(position);
                    adapter.notifyItemRemoved(position);

                    // Update the event in firestore
                    updateEventInFirestore(event);
                    CollectionReference notifRef = db.collection("Notifications");

                    final ArrayList<Profile> profileList = new ArrayList<>();
                    ProfileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
                    ProfileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
                        for (Profile profile : profiles) {
                            if (profile.getID().equals(event.getOrgId())) {
                                profileList.add(profile);
                            }
                        }
                    });
                    Profile organizer = profileList.get(0);
                    NotificationJ notification = new NotificationJ("Removed From Event", "Dear "+ user.getUsername()+", you have been removed from "+ event.getTitle() +".", event.getOrgId(), Timestamp.now(), event.getId(), user.getID(), false);
                    notifRef.add(notification);

                    NotifyUser notifier = new NotifyUser(requireContext());
                    notifier.sendNotification(user, "Dear "+ user.getUsername()+", you have been removed from "+ event.getTitle() +".");


                });

                profileRecycler.setAdapter(adapter);

                // Set the timer to repeat this code every 1 second
                timerHandler.postDelayed(this, 1000);
            }
        };

        // Start the timer
        timerHandler.post(entrantListRunnable);
    }

    /**
     * Updates an event in firestore.
     */
    private void updateEventInFirestore(Event event) {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event updated successfully!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event", e));
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

                        // Update the recycler view
                        if (spinner_list != null && spinner_list.getSelectedItem() != null) {
                            String currentSelection = spinner_list.getSelectedItem().toString();
                            updateRecyclerList(currentSelection);
                        }
                    }
                });
    }

    public void sendNotification(View view) {
        Spinner spinnerGroup = view.findViewById(R.id.spinnerGroup);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Waitlisted", "Enrolled", "Cancelled"}
        );

        spinnerGroup.setAdapter(groupAdapter);

        EditText editMessage = view.findViewById(R.id.editMessage);
        Button btnSendNotify = view.findViewById(R.id.btnSendNotify);
        db = FirebaseFirestore.getInstance();

        NotifyUser notifier = new NotifyUser(requireContext());

        btnSendNotify.setOnClickListener(v -> {
            String message = editMessage.getText().toString().trim();
            String group = spinnerGroup.getSelectedItem().toString();

            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Profile> recipients = new ArrayList<>();

            switch (group) {
                case "Waitlisted":
                    recipients = event.getWaitList();
                    break;
                case "Enrolled":
                    recipients = event.getWonList();
                    break;
                case "Cancelled":
                    recipients = event.getCancelList();
                    break;
            }
            if (recipients == null || recipients.isEmpty()) {
                Toast.makeText(requireContext(), "No users in this group.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Profile p : recipients) {
                if (!p.isNotificationsEnabled()) continue;
                notif = new NotificationJ(event.getTitle(), message,currentUser.getUsername(),null, event.getId(), p.getUsername(), false);

                db.collection("Profiles")
                        .document(p.getID())               // profile’s firestore id
                        .collection("Notifications")
                        .add(notif)
                        .addOnSuccessListener(docRef -> {
                            // NOW update timestamp to server time
                            docRef.update("timestamp", FieldValue.serverTimestamp());
                        });
            }


            Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_SHORT).show();

            // close popup
            notifyOverlay.setVisibility(View.GONE);
            editMessage.setText(""); // clear message

        });
    }

    /**
     * Inflates the layout for this fragment, initializes UI components,
     * and sets up listeners for the spinner and back button.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_info, container, false);

        // Get the event object
        assert getArguments() != null;
        event = getArguments().getParcelable("event");

        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        currentUser = mainActivity.currentUser;

        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();

        // Start the firestore listener for the event
        startFirestoreListener();

        // Set the event title
        TextView eventTitle = view.findViewById(R.id.event_title);
        eventTitle.setText(event.getTitle());

        // Set the spinner to display the event lists
        spinner_list = view.findViewById(R.id.spinner_list);
        entrantType = view.findViewById(R.id.entrant_type);

        List<String> statuses = Arrays.asList("Waitlisted", "Won", "Lost", "Accepted", "Cancelled");
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );

        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_list.setAdapter(spinAdapter);

        profileRecycler = view.findViewById(R.id.profile_recycler);
        profileRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Update the spinner when a different event list is selected
        spinner_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                updateRecyclerList(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });

        // Set up the back button
        Button backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        // Button to view the map with entrant locations
        Button mapBtn = view.findViewById(R.id.btn_map);
        mapBtn.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            // Pass the most up-to-date event object
            bundle.putParcelable("event", event);
            NavHostFragment.findNavController(this).navigate(R.id.action_EntrantInfoFragment_to_OrganizerMapFragment, bundle);
        });

        notifyOverlay = view.findViewById(R.id.notifyOverlay);
        Button topNotifyButton = view.findViewById(R.id.btn_notify);
        ImageButton btnCloseNotify = view.findViewById(R.id.btnCloseNotify);

        // show popup
        topNotifyButton.setOnClickListener(v -> {
            notifyOverlay.setVisibility(View.VISIBLE);
            sendNotification(view);

        });

        // hide when X pressed or after sending
        btnCloseNotify.setOnClickListener(v -> {
            notifyOverlay.setVisibility(View.GONE);
        });

        view.findViewById(R.id.notifyOverlayDim).setOnClickListener(v -> {
            notifyOverlay.setVisibility(View.GONE);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the timer to prevent memory leaks or crashing
        if (timerHandler != null) {
            timerHandler.removeCallbacks(entrantListRunnable);
        }
    }
}
