package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


/**
 * Displays a list of events. For the entrant the events he signed up for. For the organizer the events he is organizing.
 */
public class MyEventsFragment extends Fragment {
    private Button btnGotoNew;

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        Button btnGo = view.findViewById(R.id.btnGoToNew);
        String eventId = "testing";

        btnGo.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("eventId", eventId);
            NavHostFragment.findNavController(this).navigate(R.id.action_myEvents_to_MyEventDetailsFragment, args); // add this action in nav_graph
        });

        return view;
    }
}