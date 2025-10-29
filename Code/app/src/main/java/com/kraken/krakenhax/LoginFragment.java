package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

public class LoginFragment extends Fragment {
    public Button signup;
    public Button login;
    public EditText unText;
    public EditText pwdText;
    public Button guest;
    public ProfileViewModel profileModel;

    public LoginFragment(){

    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){


        View view = getLayoutInflater().inflate(R.layout.fragment_login, container, false);
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileModel.addProfile(new Profile("test", "test", "Entrant", "test" + "@gmail.com"));
        profileModel.addProfile(new Profile("test2", "test2", "Organizer", "test2" + "@gmail.com"));
        profileModel.addProfile(new Profile("Jacob", "JLogin", "Entrant", "jmmccorm@ualberta.ca"));

        LiveData<ArrayList<Profile>> profileList = profileModel.getProfileList();
        signup = view.findViewById(R.id.signup_button);
        login = view.findViewById(R.id.login_button);
        unText = view.findViewById(R.id.UsernameEditText);
        pwdText = view.findViewById(R.id.PasswordEditText);
        guest = view.findViewById(R.id.guest_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Profile user : profileList.getValue()){
                    if (user.getUsername() == unText.getText().toString() && user.getPassword() == pwdText.getText().toString()){
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.currentUser = user;
                        mainActivity.loggedIn = true;
                        if (Objects.equals(user.getType(), "Organizer")){
                            //replace with organizer fragments
                            mainActivity.navController.navigate(R.id.action_login_to_events);
                        } else if (Objects.equals(user.getType(), "Entrant")) {
                            //replace with entrant fragments
                            mainActivity.navController.navigate(R.id.action_login_to_events);
                        } else {
                            //replace with Admin fragments
                            mainActivity.navController.navigate(R.id.action_login_to_events);
                        }
                    } else {
                        System.out.println("Invalid Credentials");
                    }
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.navController.navigate(R.id.action_login_to_typeSelector);
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.currentUser = new Profile("Guest", "Guest", "Guest", "Guest" + "@gmail.com");
                mainActivity.navigateToMainContent();
            }
        });





        return view;

    }

}

