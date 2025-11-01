package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class TypeChooserFragment extends Fragment {

    private Button entrant;
    private Button organizer;

    public TypeChooserFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type, container, false);
        entrant = view.findViewById(R.id.Entrant_button);
        organizer = view.findViewById(R.id.Organizer_button);

        final NavController navController = Navigation.findNavController(view);

        entrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserType", "Entrant");
                navController.navigate(R.id.action_TypeSelector_to_SignUp, bundle);
            }
        });
        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("UserType", "Organizer");
                navController.navigate(R.id.action_TypeSelector_to_SignUp, bundle);
            }
        });
        return view;
    }
}
