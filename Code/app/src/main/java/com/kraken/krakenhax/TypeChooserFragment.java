package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class TypeChooserFragment extends Fragment{

    private Button entrant;
    private Button organizer;

    public TypeChooserFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view = getLayoutInflater().inflate(R.layout.fragment_type, container, false);
        entrant = view.findViewById(R.id.Entrant_button);
        organizer = view.findViewById(R.id.Organizer_button);

        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        entrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeChooserFragmentDirections.ActionTypeSelectorToSignUp action = TypeChooserFragmentDirections.actionTypeSelectorToSignUp("Entrant");
                navController.navigate(action);
                //navController.navigate(R.id.action_TypeSelector_to_SignUp);
            }
        });
        organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeChooserFragmentDirections.ActionTypeSelectorToSignUp action = TypeChooserFragmentDirections.actionTypeSelectorToSignUp("Organizer");
                navController.navigate(action);
                //navController.navigate(R.id.action_TypeSelector_to_SignUp);
            }
        });
        return view;
    }

}
