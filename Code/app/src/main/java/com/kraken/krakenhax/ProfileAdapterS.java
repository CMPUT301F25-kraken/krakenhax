package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ProfileAdapterS extends RecyclerView.Adapter<ProfileAdapterS.ViewHolder> {

    private final ArrayList<Profile> profileList;

    public ProfileAdapterS(ArrayList<Profile> profileList) {

        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ProfileAdapterS.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Profile profile = profileList.get(position);
        holder.nameText.setText(profile.getUsername());
        holder.emailText.setText(profile.getEmail());
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.textViewProfileName);
            emailText = itemView.findViewById(R.id.textViewProfileEmail);
        }

    }

}