package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * Displays the profile page.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get references to UI elements
        ImageView profilePic = view.findViewById(R.id.profile_pic);
        TextView username = view.findViewById(R.id.tv_username);
        TextView name = view.findViewById(R.id.tv_name_entry);
        TextView email = view.findViewById(R.id.tv_email_entry);
        TextView phoneNumber = view.findViewById(R.id.tv_phone_num_entry);

        // Set a default profile picture
        profilePic.setImageResource(R.drawable.obama);

        // Set default values for the info fields
        username.setText("Obama");
        name.setText("Barack Obama");
        email.setText("obama@obamamail.com");
        phoneNumber.setText("+1 (101) 1000-1110");
    }

}