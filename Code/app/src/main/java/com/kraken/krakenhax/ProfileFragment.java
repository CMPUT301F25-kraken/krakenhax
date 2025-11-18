package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private Uri filePath;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private ImageView profilePic;

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {
    }

    /**
     * Inflates the profile layout and initializes UI bindings, Firestore references,
     * and event listeners for updating profile details, profile image, notifications,
     * and sign-out. Also populates the inputs from the current user's profile.
     *
     * @param inflater           LayoutInflater used to inflate views in the fragment
     * @param container          Optional parent view to attach the fragment UI to
     * @param savedInstanceState Saved state bundle, if the fragment is being recreated
     * @return The root view for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profiles");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // UI bindings
        usernameView = view.findViewById(R.id.UsernameTextView);
        emailView = view.findViewById(R.id.EmailTextView);
        phoneNumberView = view.findViewById(R.id.PhoneNumberTextView);
        updateButton = view.findViewById(R.id.UpdateProfileButton);
        notificationSwitch = view.findViewById(R.id.switch_notifications);
        profilePic = view.findViewById(R.id.profile_pic);

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
//        profilePic.setImageResource(R.drawable.obama);

        // Load profile picture
        loadProfilePic();

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
        // Image picker
        ActivityResultLauncher<String> imagePicker;

        imagePicker = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                filePath = uri;
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(profilePic);
                    uploadProfilePic();
                } catch (Exception e) {
                    Log.e("ImageLoad", "Error loading image", e);
                    new AlertDialog.Builder(requireContext()).setTitle("Error").setMessage("Failed to load image. Please try again.").setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
                }
            }
        });

        uploadButton.setOnClickListener(v -> imagePicker.launch("image/*"));

        // Set up delete profile pic button
        Button deleteButton = view.findViewById(R.id.button_delete);
        deleteButton.setOnClickListener(v -> {
            profile.setPicture(null);
            deleteProfilePic();
            loadProfilePic();
        });

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
            // Clear local session immediately
            if (mainActivity != null) {
                mainActivity.currentUser = null;
                mainActivity.loggedIn = false;
            }
            // Clear device link and then navigate (handles races with auto-restore)
            DeviceIdentityManager.clearAccountLinkAsync()
                    .addOnSuccessListener(ignored -> navigateAfterSignout())
                    .addOnFailureListener(e -> {
                        Log.w("ProfileFragment", "Device unlink failed, proceeding to sign out", e);
                        navigateAfterSignout();
                    });
        });

        return view;
    }

    /**
     * Navigates away from the profile screen after the sign-out process completes.
     * Ensures the fragment is added to its activity before using the NavController.
     */
    private void navigateAfterSignout() {
        if (!isAdded()) return;
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
        navController.navigate(R.id.action_signout);
    }

    /**
     * Uploads the selected profile picture to Firebase Storage and updates the user's
     * profile document in Firestore with the resulting download URL. The upload path is
     * profile_pictures/{userId}.jpg.
     * <p>
     * Preconditions: filePath and profile with a non-null ID must be set.
     */
    public void uploadProfilePic() {
        if (filePath != null && profile != null && profile.getID() != null) {
            // Path in Firebase Storage: /profile_pictures/USER_ID.jpg
            StorageReference profilePicRef = storageRef.child("profile_pictures/" + profile.getID() + ".jpg");

            UploadTask uploadTask = profilePicRef.putFile(filePath);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // After the upload is successful, get the public download URL
                profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("Firebase", "Download URL: " + downloadUrl);

                    // Update the local profile object
                    profile.setPicture(downloadUrl);
//                    db.collection("ProfilePictures")
//                            .document(profile.getID())
//                            .set(profile);
                    // Get the profile's document ID and update the picture field in profile
                    String profileID = profile.getID();
                    db.collection("Profiles").document(profileID)
                            .update("picture", downloadUrl)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Profile picture URL updated successfully in firestore"))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error updating profile picture URL in Firestore", e));
                }).addOnFailureListener(e -> {
                    // URL couldn't be retrieved after a successful upload
                    Log.e("Firebase", "Failed to get download URL", e);
                });
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Upload failed", e);
            });
        }
    }

    /**
     * Deletes the current user's profile picture from Firebase Storage at
     * profile_pictures/{userId}.jpg. No-op if the object does not exist.
     */
    public void deleteProfilePic() {
        StorageReference profilePicRef = storageRef.child("profile_pictures/" + profile.getID() + ".jpg");
        profilePicRef.delete().addOnSuccessListener(aVoid -> {
            // Profile picture deleted successfully
            Log.d("ProfileFragment", "Profile picture deleted successfully");
        }).addOnFailureListener(e -> {
            // Error
            Log.e("Firebase", "Delete profile picture failed", e);
        });
    }

    /**
     * Loads the profile picture into the ImageView. Falls back to a placeholder image
     * when the stored URL is empty or null.
     */
    public void loadProfilePic() {
        String profilePicURL = profile.getPicture();
        if (profilePicURL == null || profilePicURL.isEmpty()) {
            profilePic.setImageResource(R.drawable.obama);
        } else {
            Picasso.get()
                    .load(profilePicURL)
                    .placeholder(R.drawable.obama)
                    .error(R.drawable.obama)
                    .fit().centerCrop()
                    .into(profilePic);
        }
    }

}
