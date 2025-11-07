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
 * Displays a list of events. For the entrant the events he signed up for. For the organizer the events he is organizing.
 */
public class MyEventsFragment extends Fragment {

    private FirebaseFirestore db;

    private ArrayList<Event> events;

    private CollectionReference eventsRef;
    private MyRecyclerViewAdapter adapter;
    private Button makeEventButton;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView recycler_view_event_list2 = view.findViewById(R.id.recycler_view_events_list2);
        recycler_view_event_list2.setLayoutManager(new LinearLayoutManager(requireContext()));

        makeEventButton = view.findViewById(R.id.MakeEventButton);

        makeEventButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_MyEventsFragment_to_CreateEventFragment);
        });

        events = new ArrayList<>();

        /**
         events.add(new Event(
         "event-004",
         "Evening Yoga",
         "Relaxing Vinyasa yoga for all ages. Bring your own mat.",
         "Wellness Center Room B",
         0
         ));
         */
        adapter = new MyRecyclerViewAdapter(events);
        recycler_view_event_list2.setAdapter(adapter);

        startFirestoreListener();

        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_id", clickedEvent);
            NavHostFragment.findNavController(this).navigate(R.id.action_myEvents_to_MyEventDetailsFragment, bundle);
        });

        return view;
    }

    private void startFirestoreListener() {
        eventsRef = db.collection("Events");
        eventsRef.addSnapshotListener((snap, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed", e);
                return;
            }
            MainActivity mainActivity = (MainActivity) getActivity();
            Profile currentUser = mainActivity.currentUser;
            if (snap != null && !snap.isEmpty()) {
                events.clear();
                for (QueryDocumentSnapshot snapshot : snap) {
                    String title = snapshot.getString("title");
                    String id = snapshot.getString("id");
                    String eventDetails = snapshot.getString("eventDetails");
                    String location = snapshot.getString("location");
                    String poster = snapshot.getString("poster");
                    String orgProfile = snapshot.getString("orgId");
                    if (Objects.equals(orgProfile, currentUser.getID())) {
                        events.add(new Event(id, title, eventDetails, location, 0, poster));
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}