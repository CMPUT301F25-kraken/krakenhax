package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class TypeChooserFragment extends Fragment {
    private Button entrant;
    private Button organizer;
    private NavController navController;
    public TypeChooserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_type, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entrant = view.findViewById(R.id.Entrant_button);
        organizer = view.findViewById(R.id.Organizer_button);

        navController = Navigation.findNavController(view);

        entrant.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("UserType", "Entrant");
            navController.navigate(R.id.action_TypeSelector_to_SignUp, bundle);
        });

        organizer.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("UserType", "Organizer");
            navController.navigate(R.id.action_TypeSelector_to_SignUp, bundle);
        });
    }

}
