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
    private ArrayList<Profile> profiles;
    private Profile profile;



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



    public static class ViewHolder {
        public TextView name;
        public TextView email;
        public ImageView profilePic;
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
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.profile_format, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.UsernameDisplay);
            viewHolder.email = view.findViewById(R.id.EmailDisplay);
            viewHolder.profilePic = view.findViewById(R.id.profilePic);
            view.setTag(viewHolder);
        } else{
            viewHolder = (ViewHolder) view.getTag();
        }
        profile = profiles.get(position);
        //TextView name = view.findViewById(R.id.UsernameDisplay);
        //TextView Email = view.findViewById(R.id.EmailDisplay);
        //profilePic = view.findViewById(R.id.profilePic);
        if (profile != null) {
            loadProfilePic(profile, viewHolder.profilePic);


            viewHolder.name.setText(profile.getUsername());
            viewHolder.email.setText(profile.getEmail());
            loadProfilePic(profile, viewHolder.profilePic);
        }
        return view;
    }

    public void loadProfilePic(Profile profile, ImageView profilePic) {
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
