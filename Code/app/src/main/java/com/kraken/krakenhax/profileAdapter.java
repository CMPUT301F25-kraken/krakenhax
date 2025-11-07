package com.kraken.krakenhax;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ArrayAdapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class profileAdapter extends ArrayAdapter<Profile> {

    private ArrayList<Profile> profiles;
    private Context context;

    private Set<String> selectedProfileIds = new HashSet<>();



    public profileAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.profile_format, parent, false);
        }

        Profile profile = getItem(position);
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        TextView name = view.findViewById(R.id.UsernameDisplay);
        TextView Email = view.findViewById(R.id.EmailDisplay);
        ImageView profilePic = view.findViewById(R.id.profilePic);

        assert profile != null;
        checkBox.setChecked(selectedProfileIds.contains(profile.getID()));


        name.setText(profile.getUsername());
        Email.setText(profile.getEmail());
        profilePic.setImageResource(R.drawable.obama);


        return view;
    }

    public void toggleSelection(int position) {
        Profile profile = profiles.get(position);
        if (profile != null) {
            String profileId = profile.getID();
            if (selectedProfileIds.contains(profileId)) {
                selectedProfileIds.remove(profileId);
            } else {
                selectedProfileIds.add(profileId);
            }
            notifyDataSetChanged();
        }
    }

    public Set<String> getSelectedProfileIds() {
        return selectedProfileIds;
    }

    public void clearSelection() {
        selectedProfileIds.clear();
        notifyDataSetChanged();
    }

}
