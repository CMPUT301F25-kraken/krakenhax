package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


/**
 * A {@link Fragment} for displaying and updating the current user's profile information.
 * Allows the user to edit their username, email, and phone number, and to sign out.
 */
public class ProfileFragment extends Fragment {
    public EditText usernameView;
    public EditText EmailView;
    public EditText PhoneNumberView;
    public Button updateButton;
    public Profile profile;

    private FirebaseFirestore db;
    private CollectionReference ProfileRef;

    /**
     * Required empty public constructor for fragment instantiation.
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Inflates the layout for this fragment, initializes UI components and Firestore,
     * populates the views with the current user's data, and sets up listeners for profile updates and sign-out.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();
        ProfileRef = db.collection("Profiles");
        usernameView = view.findViewById(R.id.UsernameTextView);
        EmailView = view.findViewById(R.id.EmailTextView);
        PhoneNumberView = view.findViewById(R.id.PhoneNumberTextView);
        updateButton = view.findViewById(R.id.UpdateProfileButton);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            profile = mainActivity.currentUser;
        }
        usernameView.setText(profile.getUsername());
        EmailView.setText(profile.getEmail());
        String phoneNumber = profile.getPhoneNumber();
        if (Objects.equals(phoneNumber, "0")) {
            PhoneNumberView.setHint("No Phone Number");
            PhoneNumberView.setText("");
        } else {
            PhoneNumberView.setText(profile.getPhoneNumber());
        }
        ImageView profilePic = view.findViewById(R.id.profile_pic);

        // Set a default profile picture
        profilePic.setImageResource(R.drawable.obama);

        updateButton.setOnClickListener(v -> {
            String newUsername = usernameView.getText().toString();
            String newEmail = EmailView.getText().toString();
            String phoneNumberStr = PhoneNumberView.getText().toString();

            profile.setUsername(newUsername);
            profile.setEmail(newEmail);

            try {
                profile.setPhoneNumber(phoneNumberStr);
            } catch (NumberFormatException e) {
                profile.setPhoneNumber("0");
            }
            assert mainActivity != null;
            mainActivity.currentUser = profile;
            String ID = String.valueOf(profile.getID());
            ProfileRef.document(ID).set(profile);

        });

        Button signoutButton = view.findViewById(R.id.button_signout);
        signoutButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
            navController.navigate(R.id.action_signout);
        });

        return view;
    }

}
