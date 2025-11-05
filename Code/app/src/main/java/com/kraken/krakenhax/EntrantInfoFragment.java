package com.kraken.krakenhax;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


public class EntrantInfoFragment extends Fragment {

    private Event event;

    private Button backBtn;

    private Spinner spinner_list;

    private TextView entrantType;

    private TextView eventTitle;

    public EntrantInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_entrant_info, container, false);
        backBtn = view.findViewById(R.id.backBtn);
        assert getArguments() != null;
        event = getArguments().getParcelable("event");
        spinner_list = view.findViewById(R.id.spinner_list);
        entrantType = view.findViewById(R.id.entrant_type);
        eventTitle = view.findViewById(R.id.event_title);

        eventTitle.setText(event.getTitle());

        List<String> statuses = Arrays.asList("Waitlisted", "Enrolled", "Cancelled", "Selected");
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                statuses
        );
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_list.setAdapter(spinAdapter);

        spinner_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                entrantType.setText("Entrant " + selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case where nothing is selected
            }
        });


        backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        return view;


    }
}