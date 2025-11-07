package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CreateEventFragment extends Fragment {
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Event event;
    private EditText eventTitle;
    private TextInputEditText eventDescription;
    private EditText eventAddress;
    private EditText winnerNumber;
    private EditText waitingListCap;
    private ImageView eventPoster;
    private Button dateTimeButton;
    private FloatingActionButton uploadPosterButton;
    private Switch geolocationSwitch;
    private Button confirmButton;
    private ActivityResultLauncher<String> imagePicker;
    private Uri filePath;
    private Button backButton;
    private EventViewModel eventViewModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(eventPoster);
                    uploadPosterForEvent();
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image", e);
                    new AlertDialog.Builder(requireContext()).setTitle("Error").setMessage("Failed to load image. Please try again.").setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_fragment, container, false);
        eventTitle = view.findViewById(R.id.EventNameEditText);
        eventDescription = view.findViewById(R.id.EventDescriptionEditText);
        eventAddress = view.findViewById(R.id.EventAddressEditText);
        winnerNumber = view.findViewById(R.id.WinnerNumberEditText);
        waitingListCap = view.findViewById(R.id.EventCapEditText);
        eventPoster = view.findViewById(R.id.imagePosterView);
        dateTimeButton = view.findViewById(R.id.ChangeDateTimeButton);
        uploadPosterButton = view.findViewById(R.id.UploadPosterButton);
        geolocationSwitch = view.findViewById(R.id.GeolocationSwitch);
        confirmButton = view.findViewById(R.id.ConfirmEditsButton);
        backButton = view.findViewById(R.id.BackButton);


        return view;
    }



    public void uploadPosterForEvent() {
        if (filePath != null) {
            StorageReference eventPosterRef = storageRef.child("event_posters/" + event.getId() + ".jpg");
            UploadTask uploadTask = eventPosterRef.putFile(filePath);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                eventPosterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);
                    event.setPoster(downloadUrl);
                    db.collection("events")
                            .document(event.getId())
                            .set(event);
                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Upload failed", e);
            });
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        navController = Navigation.findNavController(view);
        eventViewModel = new EventViewModel();
        event = new Event();
        eventTitle.setOnClickListener(v -> {
            event.setTitle(eventTitle.getText().toString());
        });
        eventDescription.setOnClickListener(v -> {
            event.setEventDetails(eventDescription.getText().toString());
        });
        eventAddress.setOnClickListener(v -> {
            event.setLocation(eventAddress.getText().toString());
        });
        winnerNumber.setOnClickListener(v -> {
            event.setWinnerNumber(Integer.parseInt(winnerNumber.getText().toString()));
        });
        waitingListCap.setOnClickListener(v -> {
            event.setWaitListCap(Integer.parseInt(waitingListCap.getText().toString()));
        });
        geolocationSwitch.setOnClickListener(v -> {
            event.setUseGeolocation(geolocationSwitch.isChecked());
        });
        dateTimeButton.setOnClickListener(v -> {
            MaterialDatePicker<androidx.core.util.Pair<Long, Long>> dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select Event Timeframe")
                    .build();
            dateRangePicker.addOnPositiveButtonClickListener(selection -> {
                long startDateMillis = selection.first;
                long endDateMillis = selection.second;

                ZonedDateTime startDate = Instant.ofEpochMilli(startDateMillis).atZone(ZoneId.systemDefault());
                ZonedDateTime endDate = Instant.ofEpochMilli(endDateMillis).atZone(ZoneId.systemDefault());

                ArrayList<ZonedDateTime> timeframe = new ArrayList<>();
                timeframe.add(startDate);
                timeframe.add(endDate);
                event.setTimeframe(timeframe);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy)");
                dateTimeButton.setText(String.format("%s - %s", startDate.format(formatter), endDate.format(formatter)));

            });
            dateRangePicker.show(getParentFragmentManager(), "dateRangePicker");
        });
        uploadPosterButton.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });
        backButton.setOnClickListener(v -> {
            navController.navigate(R.id.MyEventsFragment);
        });
        confirmButton.setOnClickListener(v -> {
            Event eventToSave = new Event();
            eventToSave.setTitle(eventTitle.getText().toString());
            eventToSave.setEventDetails(eventDescription.getText().toString());
            eventToSave.setLocation(eventAddress.getText().toString());
            eventToSave.setWinnerNumber(Integer.parseInt(winnerNumber.getText().toString()));
            eventToSave.setUseGeolocation(geolocationSwitch.isChecked());
            eventToSave.setTimeframe(event.getTimeframe());
            String waitListCapStr = waitingListCap.getText().toString();
            if (!waitListCapStr.isEmpty()) {
                try {
                    eventToSave.setWaitListCap(Integer.parseInt(waitListCapStr));
                } catch (NumberFormatException e) {
                    Log.e("CreateEvent", "Invalid number for waitlist cap", e);
                    // Optionally, show an error to the user
                }
            }
            db.collection("events").document(eventToSave.getId()).set(eventToSave)
                    .addOnSuccessListener(aVoid -> {
                        // SUCCESS: The event is saved. Now navigate back.
                        Log.d("Firestore", "Event successfully created with ID: " + eventToSave.getId());
                        navController.navigate(R.id.MyEventsFragment);                    })
                    .addOnFailureListener(e -> {
                        // FAILURE: Show an error to the user.
                        Log.e("Firestore", "Error creating event", e);
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Error")
                                .setMessage("Could not save the event. Please try again.")
                                .setPositiveButton("OK", null)
                                .show();
                    });
        });


    }
}
