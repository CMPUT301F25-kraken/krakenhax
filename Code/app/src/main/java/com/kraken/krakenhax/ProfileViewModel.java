package com.kraken.krakenhax;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;


public class ProfileViewModel extends ViewModel {
    private static final MutableLiveData<ArrayList<Profile>> profileList = new MutableLiveData<>(new ArrayList<>());

    public static LiveData<ArrayList<Profile>> getProfileList() {
        return profileList;
    }

    public void addProfile(Profile profile) {
        ArrayList<Profile> currentList = profileList.getValue();
        if (currentList != null) {
            currentList.add(profile);
            profileList.setValue(currentList);
        }
    }

    public void getUsers() {
        ArrayList<Profile> currentList = profileList.getValue();
        if (currentList != null) {
            for (Profile profile : currentList) {
                System.out.println(profile.getUsername());
            }
        }
    }

}
