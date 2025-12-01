package com.kraken.krakenhax;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Dialog fragment that displays a chip list of categories and lets the user
 * select up to a maximum number of categories to filter events.
 */
public class FilterDialogFragment extends DialogFragment {


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
        void onSave(ArrayList<String> selectedCategories, ArrayList<Timestamp> availability);
    }

    private Event event;
    private ArrayList<String> categories;
    private OnFilterListener listener;
    private ArrayList<Timestamp> availability;
    private static final int MAX_CATEGORIES = 5;
    private boolean showAvailability;


    /**
     * Creates a new instance of {@link FilterDialogFragment}.
     *
     * @param categories list of category names to display as selectable chips
     * @param listener   callback invoked when the user applies the selected filters
     */
    public FilterDialogFragment(ArrayList<String> categories, OnFilterListener listener) {
        this.categories = categories;
        this.listener = listener;
        this.showAvailability = true;
        this.availability = new ArrayList<>();
    }

    public FilterDialogFragment(ArrayList<String> categories, OnFilterListener listener, boolean showAvailability) {
        this.categories = categories;
        this.listener = listener;
        this.showAvailability = showAvailability;
        if (showAvailability) {
            this.availability = new ArrayList<>();
        }
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
        Button chooseAvailabilityButton = view.findViewById(R.id.select_availability_button);

        if (showAvailability) {
            chooseAvailabilityButton.setVisibility(View.VISIBLE);
        } else {
            chooseAvailabilityButton.setVisibility(View.GONE);
        }

        chooseAvailabilityButton.setOnClickListener(v -> {
            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Select an availability date range");
            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection.first != null && selection.second != null) {
                    availability.clear();
                    availability.add(new Timestamp(new Date(selection.first)));
                    availability.add(new Timestamp(new Date(selection.second)));
                    Toast.makeText(getContext(), "Availability range selected", Toast.LENGTH_SHORT).show();
                }
            });

            picker.show(getParentFragmentManager(), "DATE_PICKER");
        });


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

                    listener.onSave(selectedCategories, availability);
                })
                .setNegativeButton("Cancel", null)
                .create();
    }


}
