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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Profile adapter for the Admin list of profiles.
 */
public class AdminProfileAdapter extends ArrayAdapter<Profile> {
    private final ArrayList<Profile> profiles;
    private Profile profile;
    public ImageView profilePic;


    /**
     * Required public constructor
     * Takes context and an arraylist of profiles.
     *
     * @param context
     * @param profiles
     */
    public AdminProfileAdapter(Context context, ArrayList<Profile> profiles) {
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

        profile = getItem(position);
        TextView name = view.findViewById(R.id.UsernameDisplay);
        TextView Email = view.findViewById(R.id.EmailDisplay);
        profilePic = view.findViewById(R.id.profilePic);
        loadProfilePic();


        name.setText(profile.getUsername());
        Email.setText(profile.getEmail());
        loadProfilePic();

        return view;
    }

    public void loadProfilePic() {
        String profilePicURL = profile.getPicture();
        if (profilePicURL == null || profilePicURL.isEmpty()) {
            profilePic.setImageResource(R.drawable.obama);
        } else {
            Picasso.get()
                    .load(profilePicURL)
                    .placeholder(R.drawable.obama)
                    .error(R.drawable.obama)
                    .fit().centerCrop()
                    .into(profilePic);
        }
    }





}
