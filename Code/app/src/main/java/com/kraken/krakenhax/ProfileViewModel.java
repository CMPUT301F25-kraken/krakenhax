package com.kraken.krakenhax;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class ProfileViewModel extends ViewModel {
    private static MutableLiveData<ArrayList<Profile>> profileList = new MutableLiveData<>(new ArrayList<>());

    public static LiveData<ArrayList<Profile>> getProfileList() {
        return profileList;
    }

    public CollectionReference profileCollection;

    public ProfileViewModel() {
        profileList = new MutableLiveData<>();
        // Initialize the Firestore database and get the "Profiles" collection reference.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        profileCollection = db.collection("Profiles");

        // Start listening for real-time updates from Firestore.
        addSnapshotListener();
    }

    public void addProfile(Profile profile) {
        ArrayList<Profile> currentList = profileList.getValue();
        if (currentList != null) {
            currentList.add(profile);
            profileList.setValue(currentList);
        }
    }

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
}
