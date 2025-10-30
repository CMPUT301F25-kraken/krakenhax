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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignUpFragment extends Fragment {

    public Button signup;
    private FirebaseFirestore db;

    private CollectionReference ProfileRef;
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
        db = FirebaseFirestore.getInstance();
        ProfileRef = db.collection("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_signup, container, false);
        profileModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        SignUpFragmentArgs args = SignUpFragmentArgs.fromBundle(getArguments());

        userType = args.getUserType();
        signup = view.findViewById(R.id.signup_button);
        username = view.findViewById(R.id.UsernameSetText);
        password = view.findViewById(R.id.PasswordSetText);
        email = view.findViewById(R.id.EmailSetText);
        LiveData<ArrayList<Profile>> profileList = profileModel.getProfileList();
        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);
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
                DocumentReference docRef = ProfileRef.document(profile.getUsername());
                docRef.set(profile);
                MainActivity mainActivity = (MainActivity) getActivity();
                assert mainActivity != null;
                mainActivity.currentUser = profile;
                mainActivity.loggedIn = true;
                navController.navigate(R.id.action_SignUp_to_Events);
            }
        });
        return view;
    }
}
