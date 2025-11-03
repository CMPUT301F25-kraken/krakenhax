package com.kraken.krakenhax;

import android.os.Bundle;
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


/**
 * The Event Page
 */
public class EventFragment extends Fragment {

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the event object passed from the other fragment
        assert getArguments() != null;
        Event event = getArguments().getParcelable("event_name");

        // Set up nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set the textview to display the correct event name
        TextView tvEventName = view.findViewById(R.id.tv_event_name);
        assert event != null;
        tvEventName.setText(event.getTitle());

        // Set up on click listener for button to go back to events view
        Button buttonBack = view.findViewById(R.id.button_back);
        buttonBack.setOnClickListener(v -> {
            navController.navigate(R.id.action_EventFragment_to_EventsFragment);
        });
    }
}