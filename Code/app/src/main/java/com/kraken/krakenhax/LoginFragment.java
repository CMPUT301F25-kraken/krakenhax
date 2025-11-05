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

public class LoginFragment extends Fragment {
    // Keep UI elements private
    private Button signup;
    private Button login;
    private EditText unText;
    private EditText pwdText;
    private Button guest;
    private ProfileViewModel profileModel;
    private NavController navController;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

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
                    } else { // Includes Admin
                        navController.navigate(R.id.action_login_to_events);
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

