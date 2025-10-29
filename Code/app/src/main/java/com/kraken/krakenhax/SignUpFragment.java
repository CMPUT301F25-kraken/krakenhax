package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class SignUpFragment extends Fragment {

    public Button signup;
    public EditText username;
    public EditText password;
    public EditText email;
    public ProfileViewModel profileModel;
    private String userType;

    public SignUpFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userType = SignUpFragmentArgs.fromBundle(getArguments()).getUserType();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_signup, container, false);
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        signup = view.findViewById(R.id.signup_button);
        username = view.findViewById(R.id.UsernameSetText);
        password = view.findViewById(R.id.PasswordSetText);
        email = view.findViewById(R.id.EmailSetText);
        LiveData<ArrayList<Profile>> profileList = profileModel.getProfileList();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Profile profile : profileList.getValue()) {
                    if (profile.getUsername().equals(username.getText().toString())) {
                        System.out.println("Username already exists");
                    }
                }
                Profile profile = new Profile(username.getText().toString(), password.getText().toString(), email.getText().toString(), userType);
                profileModel.addProfile(profile);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.currentUser = profile;
                mainActivity.loggedIn = true;
                mainActivity.navController.navigate(R.id.action_SignUp_to_Events);
            }
        });
        return view;
    }
}
