package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class LoginFragment extends Fragment {
    public Button signup;
    public Button login;
    public EditText unText;
    public EditText pwdText;
    public Button guest;
    public ProfileViewModel profileModel;
    private FirebaseFirestore db;
    private CollectionReference ProfileRef;


    public LoginFragment(){

    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        ProfileRef = db.collection("Users");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){


        View view = getLayoutInflater().inflate(R.layout.fragment_login, container, false);
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileModel.addProfile(new Profile("test", "test", "Entrant", "test" + "@gmail.com"));
        profileModel.addProfile(new Profile("test2", "test2", "Organizer", "test2" + "@gmail.com"));
        profileModel.addProfile(new Profile("Jacob", "JLogin", "Entrant", "jmmccorm@ualberta.ca"));
        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);



        ProfileRef.addSnapshotListener((value, error) -> {
            if (error != null){
                Log.e("Firestone",error.toString());
            }
            if (value != null &&!value.isEmpty()){
                for (QueryDocumentSnapshot snapshot : value){
                    String username = snapshot.getString("username");
                    String password = snapshot.getString("password");
                    String email = snapshot.getString("email");
                    String type = snapshot.getString("type");
                    profileModel.addProfile(new Profile(username, password, type, email));
                }
            }
        });

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
                    if (Objects.equals(user.getUsername(), unText.getText().toString()) && Objects.equals(user.getPassword(), pwdText.getText().toString())){
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        mainActivity.currentUser = user;
                        mainActivity.loggedIn = true;
                        if (Objects.equals(user.getType(), "Organizer")){
                            //replace with organizer fragments
                            navController.navigate(R.id.action_login_to_events);
                        } else if (Objects.equals(user.getType(), "Entrant")) {
                            //replace with entrant fragments
                            navController.navigate(R.id.action_login_to_events);
                        } else {
                            //replace with Admin fragments
                            navController.navigate(R.id.action_login_to_events);
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
                navController.navigate(R.id.action_login_to_typeSelector);
            }
        });

        guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.currentUser = new Profile("Guest", "Guest", "Guest", "Guest" + "@gmail.com");
                navController.navigate(R.id.action_login_to_events);
            }
        });
        return view;

    }

}

