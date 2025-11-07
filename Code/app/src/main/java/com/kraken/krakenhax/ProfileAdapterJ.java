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
 * Custom ArrayAdapter for displaying a list of Profile objects.
 * This adapter is used to populate a ListView with profile information, including a checkbox for selection.
 */
public class ProfileAdapterJ extends ArrayAdapter<Profile> {

    private final ArrayList<Profile> profiles;
    private final Context context;

    private final Set<String> selectedProfileIds = new HashSet<>();

    /**
     * Constructor for the ProfileAdapterJ.
     * @param context The current context.
     * @param profiles The list of profiles to be displayed.
     */
    public ProfileAdapterJ(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        this.profiles = profiles;
        this.context = context;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
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
     * Toggles the selection state of a profile at a given position.
     * @param position The position of the profile to toggle.
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
     * Gets the set of currently selected profile IDs.
     * @return A Set of strings containing the unique IDs of the selected profiles.
     */
    public Set<String> getSelectedProfileIds() {
        return selectedProfileIds;
    }

    /**
     * Clears the current selection of profiles.
     */
    public void clearSelection() {
        selectedProfileIds.clear();
        notifyDataSetChanged();
    }

}
