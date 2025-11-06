package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class AdminListFragment extends Fragment {
    private MyRecyclerViewAdapter adapter;

    public AdminListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_list, container, false);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
        Spinner spinner = view.findViewById(R.id.spinner_admin_lists);
        String[] spinnerList = {"Entrants", "Organizers", "Events", "Photos"};

        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(SpinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(requireContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        


        // Set up recycler view
        RecyclerView recycler_view_event_list = view.findViewById(R.id.recycler_view_admin_lists);
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

        // DEMO ORGANIZER LOGIC
        Event testEvent = demo_list.get(0);

        // Creating demo entrant profiles
        Profile entrant1 = new Profile("2","Amaan", "1234", "Entrant", "amaaniqb@ualberta.ca","0");
        Profile entrant2 = new Profile("3","Markus", "abcd", "Entrant", "mhenze@ualberta.ca","0");
        Profile entrant3 = new Profile("4","Logan", "pass", "Entrant", "lapope@ualberta.ca","0");

        // Add to event waitlist
        testEvent.getWaitList().addEntrant(entrant1);
        testEvent.getWaitList().addEntrant(entrant2);
        testEvent.getWaitList().addEntrant(entrant3);

        // Organizer picks one as winner
        testEvent.getWonList().addWinner(entrant1);

        // One entrant cancels
        testEvent.getCancelList().addCancelled(entrant2);

        // Draw replacement (Story 30)
        if (!testEvent.getWaitList().isEmpty()) {
            Profile replacement = testEvent.getWaitList().getEntrants().get(0);
            testEvent.getWonList().addWinner(replacement);
            testEvent.getWaitList().removeEntrant(replacement);
        }

        // Notify users
        NotifyUser notifyUser = new NotifyUser();
        notifyUser.sendNotification(entrant1, "You’ve been accepted into " + testEvent.getTitle() + "!");
        notifyUser.sendNotification(entrant2, "You’ve been cancelled from " + testEvent.getTitle() + ".");
        notifyUser.sendNotification(entrant3, "You’ve been moved from waitlist to accepted!");


        adapter = new MyRecyclerViewAdapter(demo_list);
        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event_name", clickedEvent);
            navController.navigate(R.id.action_EventsFragment_to_EventFragment, bundle);
        });

        recycler_view_event_list.setAdapter(adapter);

    }
}
