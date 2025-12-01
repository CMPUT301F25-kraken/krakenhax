package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Comparator;


/**
 * The home page for the app. Shows a list of events.
 */
public class EventsFragment extends Fragment {
    private MyRecyclerViewAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<Event> events;
    private ArrayList<Event> allEvents;
    private Profile currentUser;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned,
     * but before any saved state has been restored in to the view.
     * This is where UI components are initialized and listeners are set up.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button notifications = view.findViewById(R.id.notifications);
        ImageButton filterEventsButton = view.findViewById(R.id.filter_events_button);


        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }
        startNotificationListener();
        // Set up nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set up recycler view for events list
        RecyclerView recycler_view_event_list = view.findViewById(R.id.recycler_view_events_list);
        recycler_view_event_list.setLayoutManager(new LinearLayoutManager(requireContext()));
        events = new ArrayList<>();
        allEvents = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        adapter = new MyRecyclerViewAdapter(events);
        recycler_view_event_list.setAdapter(adapter);

        // Set up a firebase listener to get the events
        startFirestoreListener();

        // Set an on item click listener for the recycler view
        // When an event is clicked on
        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", clickedEvent);
            navController.navigate(R.id.action_EventsFragment_to_EventFragment, bundle);
        });

        notifications.setOnClickListener(v -> {
            navController.navigate(R.id.action_EventsFragment_to_NotificationFragment);
        });

        Event event = new Event();
        ArrayList<String> categories = event.getAvailableCategories();

        filterEventsButton.setOnClickListener(v -> {
            FilterDialogFragment filterDialogFragment = new FilterDialogFragment(categories, selectedCategories -> {
                // Handle the selected categories here
                Log.d("EventsFragment", "Selected categories: " + selectedCategories);
                applyFilter(selectedCategories);
            });
            filterDialogFragment.show(getParentFragmentManager(), "filter_dialog");
        });
    }

    /**
     * Filters the event list based on the selected categories.
     * If no categories are selected, it displays all events.
     *
     * @param selectedCategories The list of categories to filter by.
     */
    private void applyFilter(ArrayList<String> selectedCategories) {
        // Clear the current display list
        events.clear();

        // If no categories are selected, or the list is null, show all events
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            events.addAll(allEvents);
        } else {
            // Otherwise, apply the filter
            Filter filter = new Filter(currentUser, allEvents);
            filter.getCategories().addAll(selectedCategories); // Set the categories for the filter
            filter.setFilter(); // Run the filtering logic
            events.addAll(filter.getFilteredEvents()); // Add the filtered events to the display list
        }

        // Notify the adapter that the data has changed to refresh the UI
        adapter.notifyDataSetChanged();
    }

    /**
     * Sets up a Firestore snapshot listener to get real-time updates for the events collection.
     * The events should be sorted from newest to oldest based on date created with events with
     * no date created at the very end.
     */
    private void startFirestoreListener() {
        CollectionReference eventsRef = db.collection("Events"); // Corrected to capital 'E'
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
                            allEvents.add(event);
                        }
                        // Sort the events from newest to oldest
                        allEvents.sort(Comparator.comparing(Event::getDateCreated, Comparator.nullsLast(Comparator.reverseOrder())));
                        //events.clear();
                        events.addAll(allEvents);

                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void startNotificationListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        // Get current profile id
        String profileId = currentUser.getID();

        // only unread
        // Show local notification on THIS device
        // Mark as read so we don't show it again
        ListenerRegistration notificationListener = db.collection("Profiles")
                .document(profileId)
                .collection("Notifications")
                .whereEqualTo("read", false)   // only unread
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    for (DocumentChange dc : snap.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot doc = dc.getDocument();
                            String message = doc.getString("body");
                            String eventId = doc.getString("eventId");

                            // Show local notification on THIS device
                            showLocalNotification(message);

                            // Mark as read so we don't show it again
                            doc.getReference().update("read", true);
                        }
                    }
                });
    }

    private void showLocalNotification(String message) {
        NotifyUser notifier = new NotifyUser(requireContext());
        notifier.sendNotification(currentUser, message);
    }

}
