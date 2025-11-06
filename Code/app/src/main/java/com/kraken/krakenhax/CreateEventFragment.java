package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class CreateEventFragment extends Fragment {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_fragment, container, false);
        eventTitle = view.findViewById(R.id.EventNameEditText);
        eventDescription = view.findViewById(R.id.EventDescriptionEditText);
        eventAddress = view.findViewById(R.id.EventAddressEditText);
        winnerNumber = view.findViewById(R.id.WinnerNumberEditText);
        waitingListCap = view.findViewById(R.id.EventCapEditText);
        eventPoster = view.findViewById(R.id.imageView);
        dateTimeButton = view.findViewById(R.id.ChangeDateTimeButton);
        uploadPosterButton = view.findViewById(R.id.UploadPosterButton);
        geolocationSwitch = view.findViewById(R.id.GeolocationSwitch);
        confirmButton = view.findViewById(R.id.ConfirmEditsButton);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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


    }
}
