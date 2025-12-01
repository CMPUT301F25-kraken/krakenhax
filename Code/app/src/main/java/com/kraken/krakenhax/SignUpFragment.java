package com.kraken.krakenhax;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * Fragment class to handle the sign-up process.
 * Creates new organizers or Entrants.
 * Logs the new user into the app.
 */
public class SignUpFragment extends Fragment {
    private Button signupButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private ProfileViewModel profileViewModel;
    private CollectionReference profileCollection;
    private String userType;
    private NavController navController;
    private Button Back;

    /**
     * Required empty public constructor
     */
    public SignUpFragment() {
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored.
     * Contains the main functionality of the fragment.
     * Sets up the listeners for the buttons.
     * On sign up button click, it checks if the username already exists. if it does, it shows a toast.
     * if it doesn't, it sends the username, password, and email to the createNewProfile function.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase and Navigation
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profileCollection = db.collection("Profiles");
        navController = Navigation.findNavController(view);

        // Initialize ViewModel
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        // Get arguments
        if (getArguments() != null) {
            userType = SignUpFragmentArgs.fromBundle(getArguments()).getUserType();
        }

        // Find views
        signupButton = view.findViewById(R.id.signup_button);
        Back = view.findViewById(R.id.Back2Sel);
        usernameEditText = view.findViewById(R.id.UsernameSetText);
        passwordEditText = view.findViewById(R.id.PasswordSetText);
        emailEditText = view.findViewById(R.id.EmailSetText);

        signupButton.setOnClickListener(v -> {
            // Get user input from EditTexts
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString().trim();

            // Validate user input
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return; // Stop the process
            }

            // Observe the LiveData to safely check for existing usernames
            profileViewModel.getProfileList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Profile>>() {
                @Override
                public void onChanged(ArrayList<Profile> profiles) {
                    // This block runs only when `profiles` is not null.
                    boolean usernameExists = false;
                    if (profiles != null) {
                        for (Profile p : profiles) {
                            if (p.getUsername().equalsIgnoreCase(username)) {
                                usernameExists = true;
                                break;
                            }
                        }
                    }

                    if (usernameExists) {
                        Toast.makeText(getContext(), "Username already exists. Please choose another.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Username is unique, proceed with creating the profile
                        createNewProfile(username, password, email);
                    }

                    // Important: Remove the observer after use to prevent it from firing again
                    // if the user stays on the screen and the data changes for another reason.
                    profileViewModel.getProfileList().removeObserver(this);
                }
            });
        });
        Back.setOnClickListener(v ->{
            navController.navigate(R.id.action_signup_to_TypeSelection);
        });
    }

    /**
     * Creates a new profile in Firestore and adds it to the ViewModel.
     * Firestore creates a new user ID, then its added to the profile object.
     * The profile is then added to the ViewModel.
     * Sets the current user in MainActivity.
     * Navigates to the next screen.
     *
     * @param username the desired username for the new account
     * @param password the chosen password for the new account
     * @param email    the email address to associate with the new account
     */
    private void createNewProfile(String username, String password, String email) {
        // Let Firestore generate the ID. The 'id' field in the constructor can be null or empty for now.
        Profile newProfile = new Profile("0", username, password, userType, email, "0");

        profileCollection.add(newProfile)
                .addOnSuccessListener(documentReference -> {
                    // Get the unique ID generated by Firestore
                    String firestoreId = documentReference.getId();

                    // Update the profile object with this new ID
                    newProfile.setID(firestoreId);

                    // Now, update the document in Firestore to include its own ID
                    profileCollection.document(firestoreId).update("id", firestoreId)
                            .addOnSuccessListener(aVoid -> {
                                // Add the complete profile to the local ViewModel
                                profileViewModel.addProfile(newProfile);

                                // Set the current user in MainActivity
                                MainActivity mainActivity = (MainActivity) getActivity();
                                if (mainActivity != null) {
                                    mainActivity.currentUser = newProfile;
                                    mainActivity.loggedIn = true;
                                }
                                // Link device to this new account
                                DeviceIdentityManager.updateAccountLink(firestoreId);
                                Toast.makeText(getContext(), "Sign up successful!", Toast.LENGTH_SHORT).show();

                                // Navigate to the next screen
                                navController.navigate(R.id.action_SignUp_to_Events);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

}
