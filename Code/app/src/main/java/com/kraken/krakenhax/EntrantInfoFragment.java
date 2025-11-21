package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A fragment that displays lists of entrants for a specific event.
 * It allows viewing entrants who are waitlisted, enrolled, or have cancelled.
 */
public class EntrantInfoFragment extends Fragment {
    private Event event;
    private TextView entrantType;
    private RecyclerView profileRecycler;

    public EntrantInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the recycler view to display the selected event list.
     */
    private void updateRecyclerList(String status) {
        entrantType.setText("Entrant " + status);

        // Retrieve the selected list from the event
        ArrayList<Profile> targetList;
        switch (status) {
            case "Waitlisted":
                targetList = event.getWaitList();
                break;
            case "Won":
                targetList = event.getWonList();
                break;
            case "Lost":
                targetList = event.getLostList();
                break;
            case "Accepted":
                targetList = event.getAcceptList();
                break;
            case "Cancelled":
                targetList = event.getCancelList();
                break;
            default:
                targetList = new ArrayList<>();
                break;
        }

        ProfileAdapter adapter = new ProfileAdapter(targetList);
        profileRecycler.setAdapter(adapter);
    }

    /**
     * Inflates the layout for this fragment, initializes UI components,
     * and sets up listeners for the spinner and back button.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_info, container, false);

        // Get the event object
        assert getArguments() != null;
        event = getArguments().getParcelable("event");

        // Set the event title
        TextView eventTitle = view.findViewById(R.id.event_title);
        eventTitle.setText(event.getTitle());

        // Set the spinner to display the event lists
        Spinner spinner_list = view.findViewById(R.id.spinner_list);
        entrantType = view.findViewById(R.id.entrant_type);

        List<String> statuses = Arrays.asList("Waitlisted", "Won", "Lost", "Accepted", "Cancelled");
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );

        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_list.setAdapter(spinAdapter);

        profileRecycler = view.findViewById(R.id.profile_recycler);
        profileRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Update the spinner when a different event list is selected
        spinner_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                updateRecyclerList(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });

        // Set up the back button
        Button backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp()
        );

        // Button to view the map with entrant locations
        Button mapBtn = view.findViewById(R.id.btn_map);
        mapBtn.setOnClickListener(view1 -> {
            Bundle bundle = new Bundle();
            // Pass the most up-to-date event object
            bundle.putParcelable("event", event);
            NavHostFragment.findNavController(this).navigate(R.id.action_EntrantInfoFragment_to_OrganizerMapFragment, bundle);
        });

        return view;
    }

}
