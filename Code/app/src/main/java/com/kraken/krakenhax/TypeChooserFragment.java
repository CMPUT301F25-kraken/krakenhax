package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

/**
 * Selects the type of user. Entrants or organizers.
 * Packages the type into a bundle and sends it to the SignUpFragment with safe args.
 */
public class TypeChooserFragment extends Fragment {
    private ImageButton entrant;
    private ImageButton organizer;
    private NavController navController;
    private Button back;

    /**
     * Required empty public constructor
     */
    public TypeChooserFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_type, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored.
     * Contains the main functionality of the fragment.
     * Sets up the listeners for the buttons.
     * On button click, it navigates to the SignUpFragment with the selected user type.
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *                           The fragment's view.
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entrant = view.findViewById(R.id.Entrant_button);
        back = view.findViewById(R.id.back2log);
        organizer = view.findViewById(R.id.Organizer_button);
        navController = Navigation.findNavController(view);
        back.setOnClickListener(v -> {
            navController.navigate(R.id.action_TypeSelector_to_login);
        });
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
