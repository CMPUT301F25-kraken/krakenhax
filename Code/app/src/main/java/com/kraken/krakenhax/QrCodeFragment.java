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

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.google.zxing.WriterException;

public class QrCodeFragment extends Fragment {

    private Event event;
    private EventViewModel eventViewModel;
    private Button saveQrButton;
    private Button comeBackButton;
    private ImageView displayQrImageView;
    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generate_qr, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            Log.e("QrCodeFragment", "No arguments passed to QrCodeFragment");
            return;
        }
        event = getArguments().getParcelable("event");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        saveQrButton = view.findViewById(R.id.save_qr_button);
        comeBackButton = view.findViewById(R.id.come_back_button);
        displayQrImageView = view.findViewById(R.id.qr_display_imageview);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
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
                } else {
                    Toast.makeText(getContext(), "Failed to save QR code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("QrCodeFragment", "Event or ID is null; cannot save QR code");
                Toast.makeText(getContext(), "Sorry, cannot save your QR code. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
}}