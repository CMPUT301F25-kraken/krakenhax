package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


/**
 * The home page for the app. Shows a list of events.
 */
public class EventsFragment extends Fragment {

    private MyRecyclerViewAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<Event> events;
    private CollectionReference eventsRef;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set up recycler view for events list
        RecyclerView recycler_view_event_list = view.findViewById(R.id.recycler_view_events_list);
        recycler_view_event_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        adapter = new MyRecyclerViewAdapter(events);
        recycler_view_event_list.setAdapter(adapter);

        // Set up a firebase listener to get the events
        db = FirebaseFirestore.getInstance();
        startFirestoreListener();

        // Set an on item click listener for the recycler view
        // When an event is clicked on
        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_name", clickedEvent);
            navController.navigate(R.id.action_EventsFragment_to_EventFragment, bundle);
        });
    }

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
                    events.add(event);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

}
