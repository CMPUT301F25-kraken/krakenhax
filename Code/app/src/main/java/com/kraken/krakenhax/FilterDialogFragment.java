package com.kraken.krakenhax;

import android.app.Dialog;
import android.os.Bundle;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class FilterDialogFragment extends DialogFragment {

    public interface OnFilterListener {
        void onSave(ArrayList<String> selectedCategories);
    }

    private ArrayList<String> categories;
    private OnFilterListener listener;
    private static final int MAX_CATEGORIES = 5;



    public FilterDialogFragment(ArrayList<String> categories, OnFilterListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.filter_category_dialog, null);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group);

        for (String category : categories) {
           Chip chip = new Chip(getContext());
           chip.setText(category);
           chip.setCheckable(true);
           chip.setClickable(true);
           chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
               int checkedCount = 0;
               for (int i = 0; i < chipGroup.getChildCount(); i++) {
                   if (((Chip) chipGroup.getChildAt(i)).isChecked()) {
                       checkedCount++;
                   }
               }
               if (isChecked && checkedCount > MAX_CATEGORIES) {
                   buttonView.setChecked(false);
                   Toast.makeText(getContext(), "Cannot select more than 5 categories", Toast.LENGTH_SHORT).show();
               }
           });
           chipGroup.addView(chip);
        }

        return new AlertDialog.Builder(requireContext())
                .setTitle("Choose up to 5 categories")
                .setView(view)
                .setPositiveButton("Apply", (dialog, which) -> {
                    ArrayList<String> selectedCategories = new ArrayList<>();

                    for (int i = 0; i < chipGroup.getChildCount(); i++) {
                        Chip chip = (Chip) chipGroup.getChildAt(i);
                        if (chip.isChecked()) {
                            selectedCategories.add(chip.getText().toString());
                        }
                    }

                    listener.onSave(selectedCategories);
                })
                .setNegativeButton("Cancel", null)
                .create();
    }


}
