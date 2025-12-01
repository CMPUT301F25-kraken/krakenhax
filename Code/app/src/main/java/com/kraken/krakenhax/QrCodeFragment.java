package com.kraken.krakenhax;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.zxing.WriterException;


/**
 * Fragment that generates and displays a QR code for a selected {@link Event}.
 * It also allows the user to save the generated QR code image and navigate back
 * to the My Events screen.
 */
public class QrCodeFragment extends Fragment {
    private Event event;
    private EventViewModel eventViewModel;
    private Button saveQrButton;
    private ImageView displayQrImageView;
    private NavController navController;


    /**
     * Inflates the layout used to display the QR code and its related controls.
     *
     * @param inflater           the LayoutInflater used to inflate the layout
     * @param container          the parent view that the fragment's UI should be attached to
     * @param savedInstanceState the previously saved state, if any
     * @return the root view for this fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_qr, container, false);
    }

    /**
     * Initializes the fragment and retrieves the {@link Event} argument used to
     * generate the QR code.
     *
     * @param savedInstanceState the previously saved state, if any
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            Log.e("QrCodeFragment", "No arguments passed to QrCodeFragment");
            return;
        }
        event = getArguments().getParcelable("event");
    }

    /**
     * Called after the fragment's view has been created. Sets up the UI, generates
     * and displays the QR code, requests storage permissions, and configures
     * navigation and saving behavior.
     *
     * @param view the root view of the fragment's layout
     * @param savedInstanceState the previously saved state, if any
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saveQrButton = view.findViewById(R.id.save_qr_button);
        Button comeBackButton = view.findViewById(R.id.come_back_button);
        displayQrImageView = view.findViewById(R.id.qr_display_imageview);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        navController = NavHostFragment.findNavController(this);

        if (event != null && event.getId() != null) {
            try {
                Bitmap qrCodeBitmap = eventViewModel.generateQR(event.getId());
                displayQrImageView.setImageBitmap(qrCodeBitmap);
            } catch (WriterException e) {
                Log.e("QrCodeFragment", "Error generating QR code", e);
                displayQrImageView.setImageResource(R.drawable.outline_beach_access_100);
            }
        } else {
            Log.e("QrCodeFragment", "Event or ID is null; cannot display QR code");
            displayQrImageView.setImageResource(R.drawable.outline_beach_access_100);
        }

        comeBackButton.setOnClickListener(v -> {
            // Navigate back to the my events fragment
            navController.navigate(R.id.action_QrCodeFragment_to_MyEventsFragment);
        });

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        saveQrButton.setOnClickListener(v -> {
            if (event != null && event.getId() != null) {
                Boolean isSaved = eventViewModel.saveImage(requireContext(), displayQrImageView);
                if (isSaved) {
                    Toast.makeText(getContext(), "QR code saved to gallery", Toast.LENGTH_SHORT).show();
                    saveQrButton.setBackgroundColor(getResources().getColor(R.color.gray, null));
                    saveQrButton.setText("Saved");
                    v.setEnabled(false);
                    navController.navigate(R.id.action_QrCodeFragment_to_MyEventsFragment);
                } else {
                    Toast.makeText(getContext(), "Failed to save QR code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("QrCodeFragment", "Event or ID is null; cannot save QR code");
                Toast.makeText(getContext(), "Sorry, cannot save your QR code. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}