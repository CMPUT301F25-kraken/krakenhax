package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;


/**
 * The organizer fragment
 */
public class OrganizerFragment extends Fragment {
    private FirebaseFirestore db;
    private MyRecyclerViewAdapter adapter;
    private ArrayList<Event> events;

    /**
     * Default constructor required for fragment instantiation.
     */
    public OrganizerFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the organizer fragment layout.
     *
     * @param inflater           the LayoutInflater used to inflate views in the fragment
     * @param container          the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the root view for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer, container, false);
    }

    /**
     * Called immediately after the fragment's view has been created.
     * Sets up UI components, navigation, Firestore, and the event list.
     *
     * @param view               the root view of the fragment's layout
     * @param savedInstanceState the previously saved state of the fragment, if any
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the object for the organizer profile
        assert getArguments() != null;
        Profile organizer = getArguments().getParcelable("organizer");

        // Set up the nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();

        // Set up back button
        Button back = view.findViewById(R.id.button_organizer_back);
        back.setOnClickListener(v -> {
            //navController.navigate(R.id.action_OrganizerFragment_to_EventFragment);
            navController.popBackStack();
        });

        // Set up the text view for organizer name
        TextView tvOrganizer = view.findViewById(R.id.tv_organizer_name);
        assert organizer != null;
        tvOrganizer.setText(organizer.getUsername());

        // Set up the recycler view
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_organizer);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        events = new ArrayList<>();
        adapter = new MyRecyclerViewAdapter(events);
        recyclerView.setAdapter(adapter);

        // Start the firestore listener
        startFirestoreListener(organizer.getID());

        // Set an on item click listener for the recycler view
        // When an event is clicked on
        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", clickedEvent);
            navController.navigate(R.id.action_OrganizerFragment_to_EventFragment, bundle);
        });
    }

    /**
     * Sets up a Firestore snapshot listener to get real-time updates for the "Events" collection.
     * It filters events to show only those created by the selected organizer.
     *
     * @param organizerID the ID of the organizer whose events should be displayed
     */
    private void startFirestoreListener(String organizerID) {
        CollectionReference eventsRef = db.collection("Events");
        eventsRef.orderBy("dateCreated", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Listen failed", e);
                        return;
                    }
                    if (snap != null && !snap.isEmpty()) {
                        events.clear();
                        for (QueryDocumentSnapshot doc : snap) {
                            // Use .toObject() for robust deserialization
                            Event event = doc.toObject(Event.class);

                            // Display events organized by the organizer
                            if (event.getOrgId() != null && event.getOrgId().equals(organizerID)) {
                                events.add(event);
                            }

                        }
                        // Sort the events from newest to oldest
                        events.sort(Comparator.comparing(Event::getDateCreated, Comparator.nullsLast(Comparator.reverseOrder())));

                        adapter.notifyDataSetChanged();
                    }
                });
    }

}