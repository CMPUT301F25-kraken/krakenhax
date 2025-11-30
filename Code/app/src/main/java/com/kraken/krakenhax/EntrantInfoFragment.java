package com.kraken.krakenhax;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A fragment that displays lists of entrants for a specific event.
 * It allows viewing entrants who are waitlisted, enrolled, or have cancelled.
 */
public class EntrantInfoFragment extends Fragment {
    private final Handler timerHandler = new Handler();
    public ProfileViewModel ProfileModel;
    private Event event;
    private TextView entrantType;
    private RecyclerView profileRecycler;
    private Spinner spinner_list;
    private FirebaseFirestore db;
    private Runnable entrantListRunnable;
    private View notifyOverlay;
    private Profile currentUser;
    private NotificationJ notif;

    /**
     * Default empty public constructor required for fragment instantiation.
     */
    public EntrantInfoFragment() {
        // Required empty public constructor
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

        // Get the object for the current user
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        // Create instance of firestore database
        db = FirebaseFirestore.getInstance();

        // Start the firestore listener for the event
        startFirestoreListener();

        // Set the event title
        TextView eventTitle = view.findViewById(R.id.event_title);
        eventTitle.setText(event.getTitle());

        // Set the spinner to display the event lists
        spinner_list = view.findViewById(R.id.spinner_list);
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

        notifyOverlay = view.findViewById(R.id.notifyOverlay);
        Button topNotifyButton = view.findViewById(R.id.btn_notify);
        ImageButton btnCloseNotify = view.findViewById(R.id.btnCloseNotify);

        // show popup
        topNotifyButton.setOnClickListener(v -> {
            notifyOverlay.setVisibility(View.VISIBLE);
            sendNotification(view);

        });

        // hide when X pressed or after sending
        btnCloseNotify.setOnClickListener(v -> {
            notifyOverlay.setVisibility(View.GONE);
        });

        view.findViewById(R.id.notifyOverlayDim).setOnClickListener(v -> {
            notifyOverlay.setVisibility(View.GONE);
        });
        Button export = view.findViewById(R.id.button_export);
        export.setOnClickListener(v -> {
            exportCsv();
        });

        return view;
    }

    /**
     * Called when the view hierarchy associated with this fragment is being
     * destroyed. Stops the timer to avoid leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Stop the timer to prevent memory leaks or crashing
        if (timerHandler != null) {
            timerHandler.removeCallbacks(entrantListRunnable);
        }
    }

    /**
     * Updates the recycler view to show entrants that match the given status.
     * Periodically refreshes the list using a timer.
     *
     * @param status the entrant status list to display (e.g., Waitlisted, Won)
     */
    private void updateRecyclerList(String status) {
        // STOP ANY PREVIOUS TIMER BEFORE STARTING A NEW ONE
        if (entrantListRunnable != null) {
            timerHandler.removeCallbacks(entrantListRunnable);
        }

        // Use a timer to update the info live
        entrantListRunnable = new Runnable() {
            @Override
            public void run() {
                // Check if the view is no longer valid from the fragment being destroyed
                if (getView() == null) {
                    timerHandler.removeCallbacks(this);
                    return;
                }

                entrantType.setText("Entrant " + status);
                String strList = "";

                // Retrieve the selected list from the event
                ArrayList<Profile> targetList;
                switch (status) {
                    case "Waitlisted":
                        targetList = event.getWaitList();
                        strList = "wait list";
                        break;
                    case "Won":
                        targetList = event.getWonList();
                        strList = "won list";
                        break;
                    case "Lost":
                        targetList = event.getLostList();
                        strList = "lost list";
                        break;
                    case "Accepted":
                        targetList = event.getAcceptList();
                        strList = "accept list";
                        break;
                    case "Cancelled":
                        targetList = event.getCancelList();
                        strList = "cancel list";
                        break;
                    default:
                        targetList = new ArrayList<>();
                        break;
                }

                ProfileAdapter adapter = new ProfileAdapter(targetList);

                // Set the listener for the remove button
                String finalStrList = strList;
                adapter.setOnRemoveClickListener(position -> {
                    Profile profileToRemove = targetList.get(position);

                    // Remove the user from the target list
                    Profile user = targetList.get(position);
                    targetList.remove(position);
                    adapter.notifyItemRemoved(position);

                    // Update the event in firestore
                    updateEventInFirestore(event);

                    // Add actions to users history
                    // From organizer perspective
                    String stringAction = String.format("Removed user from %s", finalStrList);
                    currentUser.updateHistory(new Action(stringAction, profileToRemove.getID(), event.getId()));
                    updateProfileInFirestore(currentUser);
                    // From entrants perspective
                    stringAction = String.format("Removed from %s", finalStrList);
                    profileToRemove.updateHistory(new Action(stringAction, currentUser.getID(), event.getId()));
                    updateProfileInFirestore(profileToRemove);
                    CollectionReference notifRef = db.collection("Profiles").document(user.getID()).collection("Notifications");

                    final ArrayList<Profile> profileList = new ArrayList<>();
                    ProfileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
                    ProfileModel.getProfileList().observe(getViewLifecycleOwner(), profiles -> {
                        for (Profile profile : profiles) {
                            if (profile.getID().equals(event.getOrgId())) {
                                profileList.add(profile);
                            }
                        }
                    });
                    Profile organizer = profileList.get(0);
                    NotificationJ notification = new NotificationJ("Removed From Event", "Dear " + user.getUsername() + ", you have been removed from " + event.getTitle() + ".", organizer.getID(), Timestamp.now(), event.getId(), user.getID(), false);
                    notifRef.add(notification);

                    //NotifyUser notifier = new NotifyUser(requireContext());
                    //notifier.sendNotification(user, "Dear " + user.getUsername() + ", you have been removed from " + event.getTitle() + ".");


                });

                profileRecycler.setAdapter(adapter);

                // Set the timer to repeat this code every 1 second
                timerHandler.postDelayed(this, 1000);
            }
        };

        // Start the timer
        timerHandler.post(entrantListRunnable);
    }

    /**
     * Updates an event in firestore.
     */
    private void updateEventInFirestore(Event event) {
        if (event != null && event.getId() != null) {
            db.collection("Events").document(event.getId()).set(event)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Event updated successfully!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating event", e));
        }
    }

    /**
     * Updates a profile in firestore.
     */
    private void updateProfileInFirestore(Profile profile) {
        if (profile != null && profile.getID() != null) {
            db.collection("Profiles").document(profile.getID()).update("history", profile.getHistory())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Updated profile: " + profile.getUsername() + " successfully!"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error updating profile: " + profile.getUsername(), e));
        }
    }

    /**
     * Set up a Firestore snapshot listener to get real-time updates for the event.
     */
    private void startFirestoreListener() {
        if (event == null || event.getId() == null) {
            return;
        }

        db.collection("Events").document(event.getId())
                .addSnapshotListener((snapshot, e) -> {
                    assert snapshot != null;
                    Event updatedEvent = snapshot.toObject(Event.class);

                    if (updatedEvent != null) {
                        this.event = updatedEvent;

                        // Update the recycler view
                        if (spinner_list != null && spinner_list.getSelectedItem() != null) {
                            String currentSelection = spinner_list.getSelectedItem().toString();
                            updateRecyclerList(currentSelection);
                        }
                    }
                });
    }

    /**
     * Configures and sends a notification to a selected group of entrants
     * based on the chosen status in the notification popup.
     *
     * @param view the root view containing the notification UI elements
     */
    public void sendNotification(View view) {
        Spinner spinnerGroup = view.findViewById(R.id.spinnerGroup);

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Waitlisted", "Enrolled", "Cancelled"}
        );

        spinnerGroup.setAdapter(groupAdapter);

        EditText editMessage = view.findViewById(R.id.editMessage);
        Button btnSendNotify = view.findViewById(R.id.btnSendNotify);
        db = FirebaseFirestore.getInstance();

        NotifyUser notifier = new NotifyUser(requireContext());

        btnSendNotify.setOnClickListener(v -> {
            String message = editMessage.getText().toString().trim();
            String group = spinnerGroup.getSelectedItem().toString();

            if (message.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Profile> recipients = new ArrayList<>();

            switch (group) {
                case "Waitlisted":
                    recipients = event.getWaitList();
                    break;
                case "Enrolled":
                    recipients = event.getWonList();
                    break;
                case "Cancelled":
                    recipients = event.getCancelList();
                    break;
            }
            if (recipients == null || recipients.isEmpty()) {
                Toast.makeText(requireContext(), "No users in this group.", Toast.LENGTH_SHORT).show();
                return;
            }

            for (Profile p : recipients) {
                if (!p.isNotificationsEnabled()) continue;
                notif = new NotificationJ(event.getTitle(), message, currentUser.getUsername(), null, event.getId(), p.getUsername(), false);

                db.collection("Profiles")
                        .document(p.getID())               // profile’s firestore id
                        .collection("Notifications")
                        .add(notif)
                        .addOnSuccessListener(docRef -> {
                            // NOW update timestamp to server time
                            docRef.update("timestamp", FieldValue.serverTimestamp());
                        });
            }

            Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_SHORT).show();

            // close popup
            notifyOverlay.setVisibility(View.GONE);
            editMessage.setText(""); // clear message
        });

    }

    /**
     * Exports the list of winning entrants to a CSV file in the device's
     * Downloads directory.
     */
    private void exportCsv() {

        if (event == null || event.getWonList() == null || event.getWonList().isEmpty()) {
            Toast.makeText(getContext(),
                    "No entrants to export", Toast.LENGTH_SHORT).show();
            return;
        }


        StringBuilder csv = new StringBuilder();
        csv.append("Name,Email,Phone\n");  // header row

        for (Profile p : event.getWonList()) {
            String name = p.getUsername();
            String email = p.getEmail();
            String phone = p.getPhoneNumber();

            if (name == null) name = "N/A";
            if (email == null) email = "N/A";
            if (phone == null) phone = "N/A";

            csv.append(name).append(",")
                    .append(email).append(",")
                    .append(phone).append("\n");
        }

        //Save to Downloads on the organizer's phone
        try {
            String eventIdPart = event.getTitle();
            if (eventIdPart == null || eventIdPart.isEmpty()) {
                eventIdPart = "event";
            }

            String fileName = "final_entrants_" + eventIdPart + ".csv";

            File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File file = new File(downloadsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csv.toString().getBytes());
            fos.flush();
            fos.close();

            Toast.makeText(getContext(),
                    "Saved to Downloads/" + fileName,
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(),
                    "Error saving CSV file", Toast.LENGTH_SHORT).show();
        }
    }

}
