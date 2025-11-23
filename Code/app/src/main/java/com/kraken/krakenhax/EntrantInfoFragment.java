package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A fragment that displays lists of entrants for a specific event.
 * It allows viewing entrants who are waitlisted, enrolled, or have cancelled.
 */
public class EntrantInfoFragment extends Fragment {
    private final android.os.Handler timerHandler = new android.os.Handler();
    private Event event;
    private TextView entrantType;
    private RecyclerView profileRecycler;
    private Spinner spinner_list;
    private FirebaseFirestore db;
    private Runnable entrantListRunnable;

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
                    targetList.remove(position);
                    adapter.notifyItemRemoved(position);

                    // Update the event in firestore
                    updateEventInFirestore(event);
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
