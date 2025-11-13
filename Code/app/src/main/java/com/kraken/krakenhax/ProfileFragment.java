package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * Displays and allows editing of the current user's profile.
 * Supports updating username, email, phone number, and notification preferences.
 */
public class ProfileFragment extends Fragment {

    private EditText usernameView;
    private EditText emailView;
    private EditText phoneNumberView;
    private Button updateButton;
    private Switch notificationSwitch;
    private Profile profile;

    private FirebaseFirestore db;
    private CollectionReference profileRef;

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profiles");

        // UI bindings
        usernameView = view.findViewById(R.id.UsernameTextView);
        emailView = view.findViewById(R.id.EmailTextView);
        phoneNumberView = view.findViewById(R.id.PhoneNumberTextView);
        updateButton = view.findViewById(R.id.UpdateProfileButton);
        notificationSwitch = view.findViewById(R.id.switch_notifications);
        ImageView profilePic = view.findViewById(R.id.profile_pic);

        // Load current user profile
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            profile = mainActivity.currentUser;
        }

        // Populate fields
        usernameView.setText(profile.getUsername());
        emailView.setText(profile.getEmail());
        String phoneNumber = profile.getPhoneNumber();
        if (Objects.equals(phoneNumber, "0") || phoneNumber.isEmpty()) {
            phoneNumberView.setHint("No Phone Number");
            phoneNumberView.setText("");
        } else {
            phoneNumberView.setText(profile.getPhoneNumber());
        }

        // Set default profile picture
        profilePic.setImageResource(R.drawable.obama);

        // Initialize notification switch
        notificationSwitch.setChecked(profile.isNotificationsEnabled());
        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            profile.setNotificationsEnabled(isChecked);
            Toast.makeText(requireContext(),
                    isChecked ? "Notifications enabled" : "Notifications disabled",
                    Toast.LENGTH_SHORT).show();

            // Save preference in Firestore
            String id = String.valueOf(profile.getID());
            profileRef.document(id).update("notificationsEnabled", isChecked);
        });

        // Set up upload profile pic button
        Button uploadButton = view.findViewById(R.id.button_upload_profile_pic);
        ActivityResultLauncher<String> imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(eventPoster);
                    if (event != null) {
                        eventViewModel.uploadPosterForEvent(event, filePath);
                    } else {
                        Log.e("ImageLoad", "Event is null");
                    }
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image", e);
                    new AlertDialog.Builder(requireContext()).setTitle("Error").setMessage("Failed to load image. Please try again.").setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
                }
            }

        // Handle profile update
        updateButton.setOnClickListener(v -> {
            String newUsername = usernameView.getText().toString().trim();
            String newEmail = emailView.getText().toString().trim();
            String phoneStr = phoneNumberView.getText().toString().trim();

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Username and Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            profile.setUsername(newUsername);
            profile.setEmail(newEmail);
            try {
                profile.setPhoneNumber(phoneStr);
            } catch (NumberFormatException e) {
                profile.setPhoneNumber("0");
            }

            if (mainActivity != null) {
                mainActivity.currentUser = profile;
            }

            // Update Firestore
            String id = String.valueOf(profile.getID());
            profileRef.document(id).set(profile);

            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        // Handle sign-out button
        Button signoutButton = view.findViewById(R.id.button_signout);
        signoutButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
            navController.navigate(R.id.action_signout);
        });

        return view;
    }
}
