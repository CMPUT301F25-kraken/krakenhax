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


/**
 * A simple {@link Fragment} that allows a user to select their role type (Entrant or Organizer)
 * before proceeding to the sign-up screen.
 */
public class TypeChooserFragment extends Fragment {
    private Button entrant;
    private Button organizer;
    private NavController navController;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public TypeChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the user interface view for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_type, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned,
     * but before any saved state has been restored in to the view.
     * This is where UI components are initialized and listeners are set up.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
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
