package com.kraken.krakenhax;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ViewProfiles extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference profileRef;
    private Button backButton;
    private Button deleteButton;
    private Button deleteAccountButton;
    private StorageReference storageRef;
    private CollectionReference eventsRef;
    private Uri filePath;
    private TextView username;
    private TextView email;
    private TextView phone;
    private ImageView profilePic;
    public Profile profile;


    /**
     * Required empty public constructor
     */
    public ViewProfiles() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        assert getArguments() != null;
        profile = getArguments().getParcelable("profile");
        assert profile != null;

        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profiles");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        username = view.findViewById(R.id.UsernameTextView);
        username.setText(profile.getUsername());
        email = view.findViewById(R.id.EmailTextView);
        email.setText(profile.getEmail());
        phone = view.findViewById(R.id.PhoneNumberTextView);
        phone.setText(profile.getPhoneNumber());
        backButton = view.findViewById(R.id.back_to_list);
        deleteButton = view.findViewById(R.id.button_delete);
        deleteAccountButton = view.findViewById(R.id.button_delete_account);

        profilePic = view.findViewById(R.id.profile_pic_view);
        loadProfilePic();

        deleteButton.setOnClickListener(v -> {
            profile.setPicture(null);
            deleteProfilePic();
            loadProfilePic();
        });
        deleteAccountButton.setOnClickListener(v -> {
            deleteAccount();
            navController.navigate(R.id.action_ViewProfiles_to_AdminListFragment);
        });
        backButton.setOnClickListener(v -> navController.navigate(R.id.action_ViewProfiles_to_AdminListFragment));






    }

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
}
