package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Displays a list of events. For the entrant the events he signed up for. For the organizer the events he is organizing.
 */
public class MyEventsFragment extends Fragment {
    private Button btnFakeEvents;

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
        btnFakeEvents = view.findViewById(R.id.btnGoToNew);



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

        btnFakeEvents.setOnClickListener(v -> {
            Event fakeEvent1 = new Event(
                    "event-001",
                    " Swim Lessons",
                    "A fun week-long swimming class for kids aged 8–12.",
                    "Downtown Community Centre",
                    0,
                    null
            );
            db.collection("events").document(fakeEvent1.getId()).set(fakeEvent1)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event 1 added"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error adding event 1", e));

        });


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
                    if (snap != null && !snap.isEmpty()) {
                        events.clear();
                        for (QueryDocumentSnapshot snapshot : snap) {
                            String title = snapshot.getString("title");
                            String id = snapshot.getString("id");
                            String eventDetails = snapshot.getString("eventDetails");
                            String location = snapshot.getString("location");
                            String poster = snapshot.getString("poster");

                            events.add(new Event(id, title, eventDetails, location, 0,poster));


                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}