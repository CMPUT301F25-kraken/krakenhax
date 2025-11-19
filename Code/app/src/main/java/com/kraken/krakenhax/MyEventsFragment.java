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
import java.util.Objects;


/**
 * Displays a list of events relevant to the current user.
 * For an organizer, it shows events they have created.
 * For an entrant, it should show events they have signed up for (current implementation is for organizers).
 */
public class MyEventsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Event> events;
    private CollectionReference eventsRef;
    private MyRecyclerViewAdapter adapter;
    private Button makeEventButton;
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

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView recycler_view_event_list2 = view.findViewById(R.id.recycler_view_events_list2);
        recycler_view_event_list2.setLayoutManager(new LinearLayoutManager(requireContext()));
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        currentUser = mainActivity.currentUser;

        makeEventButton = view.findViewById(R.id.MakeEventButton);
        if (Objects.equals(currentUser.getType(), "Entrant")) {
            makeEventButton.setVisibility(View.GONE);
        }

        makeEventButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_MyEventsFragment_to_CreateEventFragment);
        });


        events = new ArrayList<>();

        adapter = new MyRecyclerViewAdapter(events);
        recycler_view_event_list2.setAdapter(adapter);

        startFirestoreListener();

        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_name", clickedEvent);
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
     * It filters events to show only those created by the current user (organizer).
     */
    private void startFirestoreListener() {
        eventsRef = db.collection("Events"); // Corrected to capital 'E'
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
                    String eventId = event.getId();
                    if (Objects.equals(orgProfile, currentUser.getID())) {
                        events.add(event);
                    }
                    if (currentUser.getMyWaitlist().contains(eventId)) {
                        events.add(event);
                    }

                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}
