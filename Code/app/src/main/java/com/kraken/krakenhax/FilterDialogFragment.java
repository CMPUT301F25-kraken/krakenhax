package com.kraken.krakenhax;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

/**
 * Dialog fragment that displays a chip list of categories and lets the user
 * select up to a maximum number of categories to filter events.
 */
public class FilterDialogFragment extends DialogFragment {
    private static final int MAX_CATEGORIES = 5;
    private Event event;
    private ArrayList<String> categories;
    private OnFilterListener listener;

    /**
     * Creates a new instance of {@link FilterDialogFragment}.
     *
     * @param categories list of category names to display as selectable chips
     * @param listener   callback invoked when the user applies the selected filters
     */
    public FilterDialogFragment(ArrayList<String> categories, OnFilterListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    /**
     * Creates the dialog containing a {@link ChipGroup} with the available
     * categories and enforces the maximum selectable categories.
     *
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the created {@link Dialog} instance to be displayed
     */
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

    /**
     * Listener interface for receiving selected categories when the
     * filter dialog is applied.
     */
    public interface OnFilterListener {
        /**
         * Called when the user confirms the dialog and applies the selected categories.
         *
         * @param selectedCategories list of category names chosen by the user
         */
        void onSave(ArrayList<String> selectedCategories);
    }

}
