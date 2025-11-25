package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;


/**
 * Displays a list of events relevant to the current user.
 * For an organizer, it shows events they have created.
 * For an entrant, it should show events they have signed up for (current implementation is for organizers).
 */
public class MyEventsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Event> events;
    private MyRecyclerViewAdapter adapter;
    private Profile currentUser;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public MyEventsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment, initializes UI components and Firestore,
     * and sets up listeners for user interactions.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();

        // Get the object for the current user
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        currentUser = mainActivity.currentUser;

        // Set up the create event button
        Button makeEventButton = view.findViewById(R.id.MakeEventButton);
        if (Objects.equals(currentUser.getType(), "Entrant")) {
            makeEventButton.setVisibility(View.GONE);
        }
        makeEventButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.action_MyEventsFragment_to_CreateEventFragment)
        );

        // Set up the recycler view to display the list of events
        RecyclerView recycler_view_event_list2 = view.findViewById(R.id.recycler_view_events_list2);
        recycler_view_event_list2.setLayoutManager(new LinearLayoutManager(requireContext()));
        events = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(events);
        recycler_view_event_list2.setAdapter(adapter);

        // Start the firestore listener
        startFirestoreListener();

        // Set on click listener for clicking on an event
        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", clickedEvent);

            // Navigate to the correct event details fragment depending on the account type
            if (Objects.equals(currentUser.getType(), "Organizer")) {
                NavHostFragment.findNavController(this).navigate(R.id.action_MyEventsFragment_to_MyEventDetailsFragment, bundle);
            } else if (Objects.equals(currentUser.getType(), "Entrant")) {
                NavHostFragment.findNavController(this).navigate(R.id.action_MyEventsFragment_to_EventFragment, bundle);
            } else if (Objects.equals(currentUser.getType(), "Guest")) {
                NavHostFragment.findNavController(this).navigate(R.id.action_MyEventsFragment_to_EventFragment, bundle);
            }
        });

        return view;
    }

    /**
     * Sets up a Firestore snapshot listener to get real-time updates for the "Events" collection.
     * It filters events to show only those created by the current user (organizer) and
     * events that the user is on any event list for.
     */
    private void startFirestoreListener() {
        CollectionReference eventsRef = db.collection("Events"); // Corrected to capital 'E'
        eventsRef.addSnapshotListener((snap, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed", e);
                return;
            }
            if (snap != null && !snap.isEmpty()) {
                events.clear();
                for (QueryDocumentSnapshot doc : snap) {
                    // Use .toObject() for robust deserialization
                    Event event = doc.toObject(Event.class);
                    String orgProfile = event.getOrgId();

                    // Display events organized by the user
                    if (Objects.equals(orgProfile, currentUser.getID())) {
                        events.add(event);
                    }

                    // Display events that the user is on any event list for
                    else if (event.getWaitList().contains(currentUser)) {
                        events.add(event);
                    } else if (event.getAcceptList().contains(currentUser)) {
                        events.add(event);
                    } else if (event.getCancelList().contains(currentUser)) {
                        events.add(event);
                    } else if (event.getLostList().contains(currentUser)) {
                        events.add(event);
                    } else if (event.getWonList().contains(currentUser)) {
                        events.add(event);
                    }

                }
                // Sort the events from newest to oldest
                events.sort(Comparator.comparing(Event::getDateCreated, Comparator.nullsLast(Comparator.naturalOrder())));

                adapter.notifyDataSetChanged();
            }
        });
    }

}
