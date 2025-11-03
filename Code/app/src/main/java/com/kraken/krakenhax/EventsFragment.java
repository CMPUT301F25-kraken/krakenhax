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

import java.util.ArrayList;


/**
 * The home page for the app. Shows a list of events.
 */
public class EventsFragment extends Fragment {

    private MyRecyclerViewAdapter adapter;

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

        // Set up recycler view
        RecyclerView recycler_view_event_list = view.findViewById(R.id.recycler_view_events_list);
        recycler_view_event_list.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayList<Event> demo_list = new ArrayList<>();
        demo_list.add(new Event("Event 1"));
        demo_list.add(new Event("Event 2"));
        demo_list.add(new Event("Event 3"));
        demo_list.add(new Event("Event 4"));
        demo_list.add(new Event("Event 5"));
        demo_list.add(new Event("Event 6"));
        demo_list.add(new Event("Event 7"));
        demo_list.add(new Event("Event 8"));

        adapter = new MyRecyclerViewAdapter(demo_list);

        // Set an on item click listener for the recycler view
        // When an event is clicked on
        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_name", clickedEvent);
            navController.navigate(R.id.action_EventsFragment_to_EventFragment, bundle);
        });

        recycler_view_event_list.setAdapter(adapter);

        //adapter.updateData(demo_list);
    }
}