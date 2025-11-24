package com.kraken.krakenhax;

import static com.google.api.ResourceProto.resource;

import android.content.Context;
import android.media.Image;
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

public class ImageAdapter extends ArrayAdapter<Image> {
    private final ArrayList<Image> imageArrayList;
    private final Set<String> selectedImageNames = new HashSet<>();
    public ImageAdapter(@NonNull Context context, ArrayList<Image> imageArrayList) {
        super(context, 0, imageArrayList);
        this.imageArrayList = imageArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_format, parent, false);
        }

        Image image = getItem(position);

        assert image != null;

        CheckBox checkBox = view.findViewById(R.id.ImageCheckBox);
        TextView Source = view.findViewById(R.id.ImaSource);
        TextView User = view.findViewById(R.id.RespUser);
        ImageView profilePic = view.findViewById(R.id.Images);



        return view;
    }
    //public void toggleSelection(int position) {
        //Image image = imageArrayList.get(position);
        //if (image != null) {

        //    if (selectedImageNames.contains(profileId)) {
        //        selectedImageNames.remove(profileId);
        //    } else {
        //        selectedImageNames.add(profileId);
        //    }
        //    notifyDataSetChanged();
        //}
   // }


    //public Set<String> getSelectedProfileIds() {
    //    return selectedImageNames;
    //}


    //public void clearSelection() {
    //    selectedImageNames.clear();
     //   notifyDataSetChanged();
    //}
}
