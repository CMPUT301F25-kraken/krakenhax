package com.kraken.krakenhax;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * ViewModel for the profiles list.
 * Kept up to date with the database.
 * Contains a list of profiles.
 */
public class ProfileViewModel extends ViewModel {
    private final FirebaseFirestore db;
    public CollectionReference profileCollection;
    private MutableLiveData<ArrayList<Profile>> profileList = new MutableLiveData<>(new ArrayList<>());

    /**
     * Required empty public constructor
     * Initializes the ViewModel and connects to the Firestore database.
     * Calls the addSnapshotListener method to start listening for real-time updates.
     */
    public ProfileViewModel() {
        // Initialize the Firestore database and get the "Profiles" collection reference.
        db = FirebaseFirestore.getInstance();
        profileCollection = db.collection("Profiles");

        // Start listening for real-time updates from Firestore.
        addSnapshotListener();
    }

    /**
     * Returns the list of profiles.
     *
     * @return the list of profiles
     */
    public LiveData<ArrayList<Profile>> getProfileList() {
        return profileList;
    }

    /**
     * Adds a profile to the list.
     *
     * @param profile the profile to add
     */
    public void addProfile(Profile profile) {
        ArrayList<Profile> currentList = profileList.getValue();
        if (currentList != null) {
            currentList.add(profile);
            profileList.setValue(currentList);
        }
    }

    /**
     * Updates the profile list in real-time.
     */
    private void addSnapshotListener() {
        // This listener will be active for the entire lifecycle of the ViewModel.
        profileCollection.addSnapshotListener((snapshots, error) -> {
            // Handle any potential errors from Firestore.
            if (error != null) {
                // In a real app, you might want to log this error or show a message.
                return;
            }

            // If there are snapshots (documents), process them.
            if (snapshots != null) {
                ArrayList<Profile> profiles = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    // Convert each document into a Profile object.
                    Profile profile = doc.toObject(Profile.class);
                    // It's good practice to ensure the auto-generated ID is set in the object.
                    profile.setID(doc.getId());
                    profiles.add(profile);
                }
                // Update the LiveData with the new list of profiles.
                // This will automatically notify any active observers (like our LoginFragment).
                profileList.setValue(profiles);
            }
        });
    }

    /**
     * Finds a profile object from a profile ID.
     */
    public void lookupProfile(String profileID, OnSuccessListener<Profile> callback) {
        List<Profile> profiles = profileList.getValue();

        // Look for profile locally
        if (profiles != null) {
            for (Profile profile : profiles) {
                if (Objects.equals(profileID, profile.getID())) {
                    callback.onSuccess(profile);
                    return;
                }
            }
        }

        // Look for the profile in firestore if it wasn't found locally
        db.collection("Profiles").document(profileID).get()
                .addOnSuccessListener(s -> {
                    if (s.exists()) {
                        Profile profile = s.toObject(Profile.class);
                        callback.onSuccess(profile);
                    } else {
                        callback.onSuccess(null); // Profile ID doesn't exist
                    }
                }).addOnFailureListener(e -> {
                    Log.e("ProfileViewModel", "Error looking up profile", e);
                    callback.onSuccess(null);
                });
    }

}
