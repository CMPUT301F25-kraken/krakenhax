package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Set;


/**
 * A fragment for administrators to view and manage lists of users (Entrants, Organizers) and events.
 */
public class AdminListFragment extends Fragment {
    private final ArrayList<Profile> EntrantList = new ArrayList<>();
    public ProfileViewModel profileModel;
    public FirebaseFirestore db;
    public ProfileAdapterJ profileAdapterJ;
    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ListView profileListView;
    private Button DelSelButton;
    private CheckBox checkBox;
    private CollectionReference profileRef;

    //private ListView profileListView;

    public AdminListFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_list, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned,
     * but before any saved state has been restored in to the view.
     * This is where UI components are initialized and listeners are set up.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
        Spinner spinner = view.findViewById(R.id.spinner_admin_lists);
        String[] spinnerList = {"Entrants", "Organizers", "Events"};
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profiles");
        recyclerView = view.findViewById(R.id.recycler_view_admin_lists);
        profileListView = view.findViewById(R.id.list_view_admin_lists);
        DelSelButton = view.findViewById(R.id.DelSelButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, spinnerList);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(SpinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(requireContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                switch (selectedItem) {
                    case "Entrants":
                        recyclerView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.VISIBLE);
                        getEntrants();
                        break;
                    case "Organizers":
                        recyclerView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.VISIBLE);
                        getOrganizers();
                        break;
                    case "Events":
                        DelSelButton.setVisibility(View.GONE);
                        profileListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        getEvents(view, navController);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        DelSelButton.setOnClickListener(v -> {

            if (profileAdapterJ == null) return;

            Set<String> selectedIds = profileAdapterJ.getSelectedProfileIds();

            if (selectedIds.isEmpty()) {
                Toast.makeText(requireContext(), "No profiles selected", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<Profile> profilesToDel = new ArrayList<>();
            for (Profile profile : EntrantList) {
                if (profile.getID() != null && selectedIds.contains(profile.getID())) {
                    profilesToDel.add(profile);
                }
            }

            for (Profile profile : profilesToDel) {
                profileRef.document(profile.getID()).delete();
                EntrantList.remove(profile);
            }

            profileAdapterJ.clearSelection();
            profileAdapterJ.notifyDataSetChanged();

            Toast.makeText(requireContext(), "Delete Selected", Toast.LENGTH_SHORT).show();

        });
    }

    /**
     * Populates the RecyclerView with a list of events and sets up a click listener.
     * @param view The current view.
     * @param navController The navigation controller for handling item clicks.
     */
    public void getEvents(View view, NavController navController) {

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
        Profile entrant1 = new Profile("2", "Amaan", "1234", "Entrant", "amaaniqb@ualberta.ca", "0");
        Profile entrant2 = new Profile("3", "Markus", "abcd", "Entrant", "mhenze@ualberta.ca", "0");
        Profile entrant3 = new Profile("4", "Logan", "pass", "Entrant", "lapope@ualberta.ca", "0");

        // Add to event waitlist
        testEvent.addToWaitList(entrant1);
        testEvent.addToWaitList(entrant2);
        testEvent.addToWaitList(entrant3);

        // Organizer picks one as winner
        testEvent.addToWonList(entrant1);

        // One entrant cancels
        testEvent.addToCancelList(entrant2);

        // Draw replacement (Story 30)
        if (!testEvent.getWaitList().isEmpty()) {
            Profile replacement = testEvent.getWaitList().get(0);
            testEvent.addToWonList(replacement);
            testEvent.removeFromWaitList(replacement);
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

            navController.navigate(R.id.action_adminListFragment_to_EventFragment, bundle);
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Fetches and displays a list of all users with the 'Entrant' type.
     */
    public void getEntrants() {
        ProfileViewModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
            EntrantList.clear();

            for (Profile profile : profiles) {
                if (profile.getType().equals("Entrant")) {
                    EntrantList.add(profile);
                }
            }

            profileAdapterJ = new ProfileAdapterJ(requireContext(), EntrantList);
            profileListView.setAdapter(profileAdapterJ);

            profileListView.setOnItemClickListener((parent, view, position, id) -> {
                profileAdapterJ.toggleSelection(position);
            });
        });
    }

    /**
     * Fetches and displays a list of all users with the 'Organizer' type.
     */
    public void getOrganizers() {
        ProfileViewModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
            EntrantList.clear();

            for (Profile profile : profiles) {
                if (profile.getType().equals("Organizer")) {
                    EntrantList.add(profile);
                }
            }

            profileAdapterJ = new ProfileAdapterJ(requireContext(), EntrantList);
            profileListView.setAdapter(profileAdapterJ);

            profileListView.setOnItemClickListener((parent, view, position, id) -> {
                profileAdapterJ.toggleSelection(position);
            });
        });
    }

}
