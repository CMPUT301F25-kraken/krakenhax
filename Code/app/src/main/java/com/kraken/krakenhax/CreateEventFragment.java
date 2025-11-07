package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class CreateEventFragment extends Fragment {
    private Button backButton;
    private Event event;
    private EventViewModel eventViewModel;
    private EditText eventTitle;
    private TextInputEditText eventDescription;
    private EditText eventLocation;
    private EditText winnerNumber;
    private EditText waitingListCap;
    private Switch geolocationSwitch;
    private Button dateTimeButton;
    private ActivityResultLauncher<String> imagePicker;
    private FloatingActionButton uploadPosterButton;
    private Uri filePath;
    private ImageView eventPoster;
    private Button confirmButton;

    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.create_event_fragment, container, false);
        backButton = view.findViewById(R.id.BackButton);
        eventTitle = view.findViewById(R.id.EventNameEditText);
        eventDescription = view.findViewById(R.id.EventDescriptionEditText);
        eventLocation = view.findViewById(R.id.EventAddressEditText);
        winnerNumber = view.findViewById(R.id.WinnerNumberEditText);
        waitingListCap = view.findViewById(R.id.EventCapEditText);
        geolocationSwitch = view.findViewById(R.id.GeolocationSwitch);
        dateTimeButton = view.findViewById(R.id.ChangeDateTimeButton);
        eventPoster = view.findViewById(R.id.imagePosterView);
        uploadPosterButton = view.findViewById(R.id.UploadPosterButton);
        confirmButton = view.findViewById(R.id.ConfirmEditsButton);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(eventPoster);
                    if (event != null) {
                        eventViewModel.uploadPosterForEvent(event, filePath);
                    } else {
                        Log.e("ImageLoad", "Event is null");
                    }
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image", e);
                    new AlertDialog.Builder(requireContext()).setTitle("Error").setMessage("Failed to load image. Please try again.").setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
                }
            }
        });
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        Profile currentUser = mainActivity.currentUser;
        event = new Event();
        event.setId(FirebaseFirestore.getInstance().collection("events").document().getId());
        event.setOrgId(currentUser.getID());
        navController = Navigation.findNavController(view);
        backButton.setOnClickListener(v -> {
            // Navigate back to the my events fragment
            navController.navigate(R.id.action_CreateEventFragment_to_MyEventsFragment);
        });

        eventTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EditText", "onTextChanged: " + s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (event != null) {
                    event.setTitle(s.toString());
                    Log.d("EditText", "afterTextChanged: " + s);
                }
            }
        });

        eventDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("TextInputEditText", "onTextChanged: " + s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (event != null) {
                    event.setEventDetails(s.toString());
                    Log.d("TextInputEditText", "afterTextChanged: " + s);
                }
            }
        });

        eventLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EditText", "onTextChanged: " + s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (event != null) {
                    event.setLocation(s.toString());
                    Log.d("EditText", "afterTextChanged: " + s);
                }
            }
        });
        winnerNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EditText", "onTextChanged: " + s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (event != null && !s.toString().isEmpty()) {
                    event.setWinnerNumber(Integer.parseInt(s.toString()));
                    Log.d("EditText", "afterTextChanged: " + s);
                }
            }
        });

        waitingListCap.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("EditText", "onTextChanged: " + s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (event != null && !s.toString().isEmpty()) {
                    event.setWaitListCap(Integer.parseInt(s.toString()));
                    Log.d("EditText", "afterTextChanged: " + s);
                }
            }
        });
        geolocationSwitch.setOnClickListener(v -> {
            if (event != null) {
                event.setUseGeolocation(geolocationSwitch.isChecked());
            }
        });
        uploadPosterButton.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });
        dateTimeButton.setOnClickListener(v -> {
            MaterialDatePicker<androidx.core.util.Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Event Timeframe")
                    .build();
            dateRangePicker.show(getParentFragmentManager(), "date_range_picker");
            dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                long startDateMillis = selection.first;
                long endDateMillis = selection.second;

                Date startDate = new Date(startDateMillis);
                Date endDate = new Date(endDateMillis);

                Timestamp startTimestamp = new Timestamp(startDate);
                Timestamp endTimestamp = new Timestamp(endDate);

                ArrayList<Timestamp> timeframe = new ArrayList<>();
                timeframe.add(startTimestamp);
                timeframe.add(endTimestamp);
                event.setTimeframe(timeframe);

                SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                dateTimeButton.setText(String.format("%s - %s", formatter.format(startDate), formatter.format(endDate)));
                startDate.toString();
                endDate.toString();
            });
        });

        confirmButton.setOnClickListener(v -> {
            if (event != null) {
                // Validate required fields before saving
                if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Event title cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
                    Toast.makeText(getContext(), "Event location cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (event.getTimeframe() == null || event.getTimeframe().isEmpty()) {
                    Toast.makeText(getContext(), "Please select a timeframe for the event", Toast.LENGTH_SHORT).show();
                    return;
                }

                // All checks passed, proceed with saving the event
                eventViewModel.addEvent(event);
                navController.navigate(R.id.action_CreateEventFragment_to_MyEventsFragment);
            } else {
                Log.e("Firebase", "Event is null");
                new AlertDialog.Builder(requireContext())
                        .setTitle("Error")
                        .setMessage("Could not create the event. Please try again.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        });
    }

}
