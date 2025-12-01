package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


/**
 * Admin fragment to display the lists of entrants, organizers, and events.
 * Will later display images and notifications.
 * Allows the admin to delete profiles.
 */
public class AdminListFragment extends Fragment {
    private final ArrayList<Profile> EntrantList = new ArrayList<>();
    private final ArrayList<Profile> OrganizerList = new ArrayList<>();
    private final ArrayList<Notification> notifList = new ArrayList<>();
    public ProfileViewModel profileModel;
    public FirebaseFirestore db;
    public AdminProfileAdapter adminProfileAdapter;
    public NotifAdapterAdmin NotifAdapter;
    private MyRecyclerViewAdapter adapter;
    private ArrayList<Event> events;
    private RecyclerView recyclerView;
    private ListView profileListView;
    private ListView NotificationListView;
    private CollectionReference profileRef;
    private CollectionReference eventsRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private static final String TAG = "MainActivityTag";


    /**
     * Required empty public constructor
     */
    public AdminListFragment() {
        // Required empty public constructor
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_list, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored.
     * Contains the main functionality of the fragment.
     * Sets up the listener for the delete button.
     * Sets up the spinner and the lists displayed by the spinner.
     * Sets up the adapter for the list view.
     * Sets up the listener for the list view.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
        Spinner spinner = view.findViewById(R.id.spinner_admin_lists);
        String[] spinnerList = {"Entrants", "Organizers", "Events", "Notifications"};
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profiles");
        recyclerView = view.findViewById(R.id.recycler_view_admin_lists);
        profileListView = view.findViewById(R.id.list_view_admin_lists);
        NotificationListView = view.findViewById(R.id.list_view_notifications);


        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

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
                        profileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
                            EntrantList.clear();

                            for (Profile profile : profiles) {
                                if (profile.getType().equals("Entrant")) {
                                    EntrantList.add(profile);
                                }
                            }
                        });
                        NotificationListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.VISIBLE);
                        getEntrants(navController, EntrantList);
                        break;
                    case "Organizers":
                        profileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
                            OrganizerList.clear();

                            for (Profile profile : profiles) {
                                if (profile.getType().equals("Organizer")) {
                                    OrganizerList.add(profile);
                                }
                            }
                        });
                        NotificationListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.VISIBLE);
                        getOrganizers(navController, OrganizerList);
                        break;
                    case "Events":
                        NotificationListView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        getEvents(navController);
                        break;
                    case "Notifications":
                        profileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
                            EntrantList.clear();

                            for (Profile profile : profiles) {
                                if (profile.getType().equals("Entrant")) {
                                    EntrantList.add(profile);
                                }
                            }
                        });
                        NotificationListView.setVisibility(View.VISIBLE);
                        profileListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        getNotifications(EntrantList);
                        NotificationListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Notification not = notifList.get(position);
                                ViewNotification dialogFragment = ViewNotification.newInstance(not);

                                dialogFragment.show(getChildFragmentManager(), "ViewNotificationDialog");

                            }

                        });
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


    }


    /**
     * Gets the events from the database and sets them up in the recyclerview. Sets up a setCLickListener to view a clicked event.
     * @param navController Takes a navController to navigate to the event fragment to get more details
     * and allow the admin to remove aspects of the event if they violate app policy.
     */
    public void getEvents(NavController navController) {
        events = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        adapter = new MyRecyclerViewAdapter(events);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        startFirestoreListener();

        adapter.setClickListener((v, position) -> {
            Event clickedEvent = adapter.getItem(position);
            Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("event", clickedEvent);

            navController.navigate(R.id.action_adminListFragment_to_EventFragment, bundle);
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Keeps the event arraylist up to date with the Firebase database.
     */
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

    /**
     * Displays a list of entrants in a ListView through a profile arrayAdapter. Sets up an on click listener for the entrant accounts to view more details.
     * @param navController Takes a navController to navigate
     * @param profileList Takes an arrayList of the entrant profile classes
     */
    public void getEntrants(NavController navController, ArrayList<Profile> profileList) {

        adminProfileAdapter = new AdminProfileAdapter(requireContext(), profileList);
        profileListView.setAdapter(adminProfileAdapter);

        profileListView.setOnItemClickListener((parent, view, position, id) -> {
            Profile clickedProfile = profileList.get(position);
            Log.d("viewProfileFragment", "You clicked " + clickedProfile.getUsername() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("profile", clickedProfile);

            navController.navigate(R.id.action_adminListFragment_to_viewProfiles, bundle);

        });
    }

    /**
     * Displays a list of Organizers in a ListView through a profile arrayAdapter. Sets up an on click listener for the Organizer accounts to view more details.
     * @param navController Takes a navController to navigate to the viewProfile fragment to view more details about a profile
     * @param profileList Takes an arrayList of organizer profile classes
     */
    public void getOrganizers(NavController navController, ArrayList<Profile> profileList) {
        adminProfileAdapter = new AdminProfileAdapter(requireContext(), profileList);
        profileListView.setAdapter(adminProfileAdapter);

        profileListView.setOnItemClickListener((parent, view, position, id) -> {
            Profile clickedProfile = profileList.get(position);
            Log.d("viewProfileFragment", "You clicked " + clickedProfile.getUsername() + " on row number " + position);
            Bundle bundle = new Bundle();
            bundle.putParcelable("profile", clickedProfile);

            navController.navigate(R.id.action_adminListFragment_to_viewProfiles, bundle);
        });
    }

    /**
     * Gets a list of notifications sent to entrants from organizers.
     * Gets an arrayList of notifications by iterating through an arrayList of entrant profiles and getting any notifications in their Notification Collections.
     * Displays the notifications through an ArrayAdapter for the notifications class and sets them up in a ListView.
     * @param profileList Takes an arrayList of entrant Profiles to get notifications from.
     */
    public void getNotifications(ArrayList<Profile> profileList) {
        notifList.clear();

        if (NotifAdapter == null) {
            NotifAdapter = new NotifAdapterAdmin(requireContext(), notifList);
            NotificationListView.setAdapter(NotifAdapter);
        } else {
            NotifAdapter.notifyDataSetChanged();
        }

        for (Profile p : profileList) {
            CollectionReference ref = db.collection("Profiles").document(p.getID()).collection("Notifications");
            ref.addSnapshotListener((snap, e) -> {
                if (e != null){
                    Log.e("Firestore", "Listen failed for user " + p.getUsername(), e);
                    return; // Stop if there's an error.
                }

                if (snap != null && !snap.isEmpty()){
                    // This loop runs when data is received from Firebase.
                    for (QueryDocumentSnapshot doc : snap){
                        Notification notification = doc.toObject(Notification.class);
                        Log.d("GetNotifications", "Found notification: " + notification.getBody());
                        notifList.add(notification);
                    }
                    if (NotifAdapter != null) {
                        NotifAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d("GetNotifications", "No notifications found for user: " + p.getUsername());
                }
            });
        }
    }

}