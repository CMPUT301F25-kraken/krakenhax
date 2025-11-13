package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


/**
 * The organizer fragment
 */
public class OrganizerFragment extends Fragment {

    public OrganizerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_organizer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the object for the organizer profile
        assert getArguments() != null;
        Profile organizer = getArguments().getParcelable("organizer");

        // Set up the nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set up back button
        Button back = view.findViewById(R.id.button_organizer_back);
        back.setOnClickListener(v -> {
            //navController.navigate(R.id.action_OrganizerFragment_to_EventFragment);
            navController.popBackStack();
        });

        // Set up the text view
        TextView tvOrganizer = view.findViewById(R.id.tv_organizer_fragment);
        String organizerUsername = organizer.getUsername();
        String organizerID = organizer.getID();
        tvOrganizer.setText(String.format("This is the organizer page for organizer: %s with ID: %s", organizerUsername, organizerID));
    }

}