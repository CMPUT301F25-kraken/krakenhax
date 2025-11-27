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
import android.widget.ImageButton;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Displays and allows editing of the current user's profile.
 * Supports updating username, email, phone number, and notification preferences.
 */
public class ProfileFragment extends Fragment {
    private EditText usernameView;
    private EditText emailView;
    private EditText phoneNumberView;
    private Profile profile;
    private FirebaseFirestore db;
    private CollectionReference profileRef;
    private Uri filePath;
    private StorageReference storageRef;
    private ImageView profilePic;
    private CollectionReference eventsRef;

    /**
     * Required empty public constructor.
     */
    public ProfileFragment() {
        // Required empty public constructor
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
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // UI bindings
        usernameView = view.findViewById(R.id.UsernameTextView);
        emailView = view.findViewById(R.id.EmailTextView);
        phoneNumberView = view.findViewById(R.id.PhoneNumberTextView);
        Button updateButton = view.findViewById(R.id.UpdateProfileButton);
        Switch notificationSwitch = view.findViewById(R.id.switch_notifications);
        profilePic = view.findViewById(R.id.profile_pic);

        // Load current user profile
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            profile = mainActivity.currentUser;
        }

        // Set up the nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

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

            // Update Firestore with specific fields only to avoid overwriting history and other data
            String id = String.valueOf(profile.getID());
            profileRef.document(id)
                    .update("username", profile.getUsername(),
                            "email", profile.getEmail(),
                            "phoneNumber", profile.getPhoneNumber())
                    .addOnSuccessListener(aVoid -> Log.d("ProfileFragment", "Profile fields updated successfully"))
                    .addOnFailureListener(e -> Log.e("ProfileFragment", "Error updating profile fields", e));

            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });

        // Handle sign-out button
        Button signoutButton = view.findViewById(R.id.button_signout);
        signoutButton.setOnClickListener(v -> signOut(mainActivity));

        // Set up delete account button
        Button buttonDeleteAccount = view.findViewById(R.id.button_delete_account);
        buttonDeleteAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("Are you sure you want to delete your account?");
            builder.setCancelable(true);

            builder.setPositiveButton(
                    "Yes, I want to delete my account.",
                    (dialog, which) -> {
                        deleteAccount();
                        Log.d("ProfileFragment", "Account deleted");
                        dialog.cancel();
                        signOut(mainActivity);
                    });

            builder.setNegativeButton(
                    "Nooooo!",
                    (dialog, which) ->
                            dialog.cancel()
            );

            AlertDialog alert = builder.create();
            alert.show();
        });

        // Set up history button
        ImageButton buttonHistory = view.findViewById(R.id.button_history);
        buttonHistory.setOnClickListener(
                v -> navController.navigate(R.id.action_ProfileFragment_to_HistoryFragment)
        );

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
            }).addOnFailureListener(e ->
                    Log.e("Firebase", "Upload failed", e)
            );
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

    public void deleteAccount() {
        String profileID = profile.getID();

        // Delete the profile document from Firestore
        profileRef = db.collection("Profiles");
        profileRef.document(profileID).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Profile with id: " + profile.getID() + " successfully deleted.");
                    Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(aVoid ->
                        Log.d("Firebase", "Delete profile with id: " + profile.getID() + " failed.")
                );

        // Delete the users profile photo
        deleteProfilePic();

        // Remove the user from the waitlists/wonlists/cancellists/lostlists/acceptlists that they are on
        // Go through all events in the firestore database
        eventsRef = db.collection("Events");
        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // For each event
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);
                boolean isUpdated = false;

                // Get all of the events lists
                ArrayList<ArrayList<Profile>> lists = new ArrayList<>();
                lists.add(event.getWaitList());
                lists.add(event.getAcceptList());
                lists.add(event.getCancelList());
                lists.add(event.getLostList());
                lists.add(event.getWonList());

                // Check if the deleted user is present in the lists
                for (ArrayList<Profile> list : lists) {
                    if (list != null) {
                        // If they are remove them
                        if (list.removeIf(p -> Objects.equals(p.getID(), profileID))) {
                            isUpdated = true;
                        }
                    }
                }

                // Update the event in firestore if it was modified
                if (isUpdated) {
                    eventsRef.document(event.getId()).set(event)
                            .addOnSuccessListener(aVoid -> Log.d("DeleteAccount", "User removed from event: " + event.getTitle()))
                            .addOnFailureListener(e -> Log.e("DeleteAccount", "Failed to update event: " + event.getTitle(), e));
                }
            }

            deleteEvents(profileID);

        }).addOnFailureListener(e ->
                Log.e("DeleteAccount", "Error: Cannot retrieve events in deleteAccount function.", e)
        );
    }

    public void deleteEvents(String profileID) {
        // Delete events that the user has created
        List<String> deletedEventIDs = new ArrayList<String>();
        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // For each event
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event event = document.toObject(Event.class);

                // Check if the event was organized by the user
                if (Objects.equals(event.getOrgId(), profileID)) {
                    deletedEventIDs.add(event.getId());
                    // If it was delete event
                    eventsRef.document(event.getId()).delete()
                            .addOnSuccessListener(aVoid -> Log.d("deleteAccount", "Event: " + event.getTitle() + " successfully deleted."))
                            .addOnFailureListener(e -> Log.e("deleteAccount", "Failed to delete event: " + event.getTitle() + ".", e));

                    // If an event was deleted we need to check each profiles myWaitlist list and remove it if necessary
                }
            }

//            // Delete event from profiles
//            if (!deletedEventIDs.isEmpty()) {
//                removeEventFromProfileMyWaitlists(deletedEventIDs);
//            }
        }).addOnFailureListener(e -> Log.e("DeleteAccount", "Error: Cannot retrieve events in deleteAccount function.", e));
    }

//    public void removeEventFromProfileMyWaitlists(List<String> deletedEventIDs) {
//        // Remove the deleted events from each profiles myWaitlist
//        profileRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
//            // For each profile
//            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                Profile profile = document.toObject(Profile.class);
//                boolean isUpdated = false;
//

    /// /                // Check if the myWaitlist contains a deleted event
    /// /                List<String> myWaitlist = profile.getMyWaitlist();
    /// /                if (myWaitlist != null) {
    /// /                    // Remove all deleted events from the waitlist
    /// /                    if (myWaitlist.removeAll(deletedEventIDs)) {
    /// /                        isUpdated = true;
    /// /                    }
    /// /                }
//
//                // Update the profile in the firestore database if it was modified
//                if (isUpdated) {
//                    profileRef.document(profile.getID()).update("myWaitList", myWaitlist)
//                            .addOnSuccessListener(aVoid -> Log.d("deleteAccount", "Profile: " + profile.getUsername() + " updated."))
//                            .addOnFailureListener(e -> Log.e("deleteAccount", "Failed to update profile: " + profile.getUsername() + ".", e));
//                }
//            }
//        }).addOnFailureListener(e -> Log.e("DeleteAccount", "Error: Cannot retrieve profiles in deleteAccount function.", e));
//    }
    public void signOut(MainActivity mainActivity) {
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

        Toast.makeText(requireContext(), "Signed out", Toast.LENGTH_SHORT).show();
    }

}
