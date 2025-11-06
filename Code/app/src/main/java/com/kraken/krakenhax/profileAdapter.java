package com.kraken.krakenhax;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;

public class profileAdapter extends ArrayAdapter<Profile> {

    public profileAdapter(@NonNull Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_format, parent, false);
        }

        Profile profile = getItem(position);

        TextView name = convertView.findViewById(R.id.UsernameDisplay);
        TextView Email = convertView.findViewById(R.id.EmailDisplay);
        ImageView profilePic = convertView.findViewById(R.id.profilePic);


        if (profile != null) {
            name.setText(profile.getUsername());
            Email.setText(profile.getEmail());
            profilePic.setImageResource(R.drawable.obama);
        }

        return convertView;
    }
}
