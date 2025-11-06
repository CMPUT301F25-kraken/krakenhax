package com.kraken.krakenhax;



import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


/**
 * Displays the profile page.
 */
public class ProfileFragment extends Fragment {
    public EditText usernameView;
    public EditText EmailView;
    public EditText PhoneNumberView;
    public Button updateButton;
    public Profile profile;

    private FirebaseFirestore db;
    private CollectionReference ProfileRef;



    /**
     * Required empty public constructor
     */
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();
        ProfileRef = db.collection("Profiles");
        usernameView = view.findViewById(R.id.UsernameTextView);
        EmailView = view.findViewById(R.id.EmailTextView);
        PhoneNumberView = view.findViewById(R.id.PhoneNumberTextView);
        updateButton = view.findViewById(R.id.UpdateProfileButton);

        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            profile = mainActivity.currentUser;
        }
        usernameView.setText(profile.getUsername());
        EmailView.setText(profile.getEmail());
        String phoneNumber = profile.getPhoneNumber();
        if (Objects.equals(phoneNumber, "0")) {
            PhoneNumberView.setHint("No Phone Number");
            PhoneNumberView.setText("");
        } else {
            PhoneNumberView.setText(profile.getPhoneNumber());
        }
        ImageView profilePic = view.findViewById(R.id.profile_pic);

        // Set a default profile picture
        profilePic.setImageResource(R.drawable.obama);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = usernameView.getText().toString();
                String newEmail = EmailView.getText().toString();
                String phoneNumberStr = PhoneNumberView.getText().toString();

                profile.setUsername(newUsername);
                profile.setEmail(newEmail);


                try {
                    profile.setPhoneNumber(phoneNumberStr);
                } catch (NumberFormatException e) {
                    profile.setPhoneNumber("0");
                }
                assert mainActivity != null;
                mainActivity.currentUser = profile;
                String ID = String.valueOf(profile.getID());
                ProfileRef.document(ID).set(profile);

            }
        });

        return view;
    }

}
