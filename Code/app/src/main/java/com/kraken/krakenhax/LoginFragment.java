package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer; // Import Observer
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The login page for the app. This is the First page the user sees when they open the app.
 * Allows users to login or sign up. If they choose to sign up, they are sent to the type selector page.
 * If they choose to login, if their user data is stored in the database, they are logged in.
 */
public class LoginFragment extends Fragment {
    // Keep UI elements private
    private Button signup;
    private Button login;
    private EditText unText;
    private EditText pwdText;
    private Button guest;
    private ProfileViewModel profileModel;
    private NavController navController;

    /**
     * Required empty public constructor
     */
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     * Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.
     * Initializes the interface and sets up listeners.
     * Contains the main functionality of the fragment.
     * On login click, it gets the info from the edit text and validates it in the validate function.
     * On signup click, it navigates to the type selector page.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Initialization ---
        navController = Navigation.findNavController(view);
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // --- Find Views ---
        signup = view.findViewById(R.id.signup_button);
        login = view.findViewById(R.id.login_button);
        unText = view.findViewById(R.id.UsernameEditText);
        pwdText = view.findViewById(R.id.PasswordEditText);
        guest = view.findViewById(R.id.guest_button);

        // --- Set Listeners ---
        login.setOnClickListener(v -> {
            String usernameInput = unText.getText().toString().trim();
            String passwordInput = pwdText.getText().toString();

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(getContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // The correct, safe way to check credentials
            validateLogin(usernameInput, passwordInput);
        });

        signup.setOnClickListener(v -> {
            navController.navigate(R.id.action_login_to_typeSelector);
        });

        guest.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.currentUser =  new Profile("5", "Guest", "Guest", "Guest", "Guest" + "@gmail.com", "0");
                mainActivity.loggedIn = true; // Make sure to set this for guest too
            }
            navController.navigate(R.id.action_login_to_events);
        });
    }

    /**
     * Validates the login information.
     * Takes the username and password from the edit text and checks them against the database.
     * If they match, the user is logged in. If not, an error message is displayed.
     * If the user is a organizer, they are navigated to the events page.
     * If the user is an entrant, they are navigated to the events page.
     * If the user is an admin, they are navigated to the admin page.
     * @param usernameInput
     * @param passwordInput
     */
    private void validateLogin(String usernameInput, String passwordInput) {
        // Get the LiveData from the ViewModel INSTANCE
        LiveData<ArrayList<Profile>> profileListLiveData = profileModel.getProfileList();

        // Observe the LiveData to safely access the list of profiles
        profileListLiveData.observe(getViewLifecycleOwner(), new Observer<>() {
            @Override
            public void onChanged(ArrayList<Profile> profiles) {
                // This code runs only when the 'profiles' list is ready.
                if (profiles == null) return; // Guard against a null list

                Profile foundUser = null;
                for (Profile user : profiles) {
                    if (Objects.equals(user.getUsername(), usernameInput) && Objects.equals(user.getPassword(), passwordInput)) {
                        foundUser = user;
                        break; // Found the user, no need to keep looping
                    }
                }

                if (foundUser != null) {
                    // --- SUCCESSFUL LOGIN ---
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity != null) {
                        mainActivity.currentUser = foundUser;
                        mainActivity.loggedIn = true;
                    }
                    Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Navigate based on user type
                    if (Objects.equals(foundUser.getType(), "Organizer")) {
                        navController.navigate(R.id.action_login_to_events);
                    } else if (Objects.equals(foundUser.getType(), "Entrant")) {
                        navController.navigate(R.id.action_login_to_events);
                    } else if (Objects.equals(foundUser.getType(), "Admin")) { // Includes Admin
                        navController.navigate(R.id.action_login_to_admin);
                    }
                } else {
                    // --- FAILED LOGIN ---
                    Toast.makeText(getContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }

                // IMPORTANT: Remove the observer so this logic doesn't re-run unexpectedly.
                // This makes the observer a "single-event" listener for this login attempt.
                profileListLiveData.removeObserver(this);
            }
        });
    }
}

