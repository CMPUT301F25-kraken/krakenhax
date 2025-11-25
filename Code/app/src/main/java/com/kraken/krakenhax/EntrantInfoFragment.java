package com.kraken.krakenhax;

import android.os.Bundle;
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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A {@link Fragment} that displays lists of entrants for a specific event.
 * It allows viewing entrants who are waitlisted, enrolled, or have cancelled.
 */
public class EntrantInfoFragment extends Fragment {
    private Event event;
    private Button backBtn;

    private Button mapBtn;

    private Spinner spinner_list;
    private TextView entrantType;
    private TextView eventTitle;
    private ProfileAdapterS adapter;

    private View notifyOverlay;

    private FirebaseStorage storage;
    private FirebaseFirestore db;

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
        backBtn = view.findViewById(R.id.backBtn);
        mapBtn = view.findViewById(R.id.btn_map);
        assert getArguments() != null;
        event = getArguments().getParcelable("event");
        spinner_list = view.findViewById(R.id.spinner_list);
        entrantType = view.findViewById(R.id.entrant_type);
        eventTitle = view.findViewById(R.id.event_title);

        eventTitle.setText(event.getTitle());

        List<String> statuses = Arrays.asList("Waitlisted", "Enrolled", "Cancelled");
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_list.setAdapter(spinAdapter);
        RecyclerView profileRecycler = view.findViewById(R.id.profile_recycler);
        profileRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        spinner_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                entrantType.setText("Entrant " + selectedItem);
                if (selectedItem.equals("Waitlisted")) {
                    adapter = new ProfileAdapterS(event.getWaitList());
                    profileRecycler.setAdapter(adapter);
                } else if (selectedItem.equals("Enrolled")) {
                    adapter = new ProfileAdapterS(event.getWonList());
                    profileRecycler.setAdapter(adapter);

                } else if (selectedItem.equals("Cancelled")) {
                    adapter = new ProfileAdapterS(event.getCancelList());
                    profileRecycler.setAdapter(adapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });

        backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
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

        return view;

    }

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

                Map<String, Object> notif = new HashMap<>();
                notif.put("message", message);
                notif.put("eventId", event.getId());
                notif.put("createdAt", FieldValue.serverTimestamp());
                notif.put("read", false);

                db.collection("Profiles")
                        .document(p.getID())               // profile’s firestore id
                        .collection("Notifications")
                        .add(notif);
            }
            //notifier.sendBroadcast(recipients, message);

            Toast.makeText(requireContext(), "Notification sent!", Toast.LENGTH_SHORT).show();

            // close popup
            notifyOverlay.setVisibility(View.GONE);
            editMessage.setText(""); // clear message

        });


    }
}
