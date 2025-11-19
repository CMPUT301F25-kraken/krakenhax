package com.kraken.krakenhax;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Profile adapter for the Admin list of profiles.
 */
public class ProfileAdapterJ extends ArrayAdapter<Profile> {
    private final ArrayList<Profile> profiles;
    private final Set<String> selectedProfileIds = new HashSet<>();

    /**
     * Required public constructor
     * Takes context and an arraylist of profiles.
     *
     * @param context
     * @param profiles
     */
    public ProfileAdapterJ(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.profiles = profiles;
    }

    /**
     * Returns the view for the list item.
     *
     * @param position
     * @param convertView
     * @param parent
     * @return the view for the list item.
     */
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

    /**
     * Toggles the selection of a profile.
     *
     * @param position
     */
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

    /**
     * Returns the set of selected profile IDs.
     *
     * @return the set of selected profile IDs.
     */
    public Set<String> getSelectedProfileIds() {
        return selectedProfileIds;
    }

    /**
     * Clears the selection of all profiles.
     */
    public void clearSelection() {
        selectedProfileIds.clear();
        notifyDataSetChanged();
    }

}
