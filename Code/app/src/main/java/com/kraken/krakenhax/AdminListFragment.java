package com.kraken.krakenhax;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
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

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Set;


/**
 * Admin fragment to display the lists of entrants, organizers, and events.
 * Will later display images and notifications.
 * Allows the admin to delete profiles.
 */
public class AdminListFragment extends Fragment {
    private final ArrayList<Profile> profileList = new ArrayList<>();
    private final ArrayList<NotificationJ> notifList = new ArrayList<>();
    public ProfileViewModel profileModel;
    public FirebaseFirestore db;
    public AdminProfileAdapter adminProfileAdapter;
    public NotifAdapterJ NotifAdapter;
    private MyRecyclerViewAdapter adapter;
    private ArrayList<Event> events;
    private RecyclerView recyclerView;
    private ListView profileListView;
    private ListView NotificationListView;
    private CollectionReference profileRef;
    private CollectionReference eventsRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;


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
        profileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
            profileList.clear();

            for (Profile profile : profiles) {
                if (profile.getType().equals("Entrant")) {
                    profileList.add(profile);
                }
            }
        });
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
                        NotificationListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.VISIBLE);
                        getEntrants(navController, profileList);
                        break;
                    case "Organizers":
                        NotificationListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.VISIBLE);
                        getOrganizers(navController, profileList);
                        break;
                    case "Events":
                        NotificationListView.setVisibility(View.GONE);
                        profileListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        getEvents(view, navController);
                        break;
                    case "Notifications":
                        NotificationListView.setVisibility(View.VISIBLE);
                        profileListView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        getNotifications(profileList);
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
     * Gets the events from the database.
     *
     * @param view
     * @param navController
     */
    public void getEvents(View view, NavController navController) {
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

    public void getNotifications(ArrayList<Profile> profileList) {
        // Get notifications from Firebase Firestore
        //CollectionReference notificationsRef = db.collection("Notifications");
        notifList.clear();
        for (Profile p : profileList) {
            if (!db.collection("Profile").document(p.getID()).collection("Notifications").get().getResult().isEmpty()) {
                CollectionReference notifRef = db.collection("Profile").document(p.getID()).collection("Notifications");
                notifRef.addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Listen failed", e);
                        return;
                    }
                    if (snap != null && !snap.isEmpty()) {
                        for (QueryDocumentSnapshot doc : snap) {
                            // Use .toObject() for robust deserialization
                            NotificationJ notification = doc.toObject(NotificationJ.class);
                            notifList.add(notification);
                        }
                        NotifAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
        NotifAdapter = new NotifAdapterJ(requireContext(), notifList);
        NotificationListView.setAdapter(NotifAdapter);
    }
}