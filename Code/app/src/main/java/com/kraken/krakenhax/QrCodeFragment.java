package com.kraken.krakenhax;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.WriterException;

public class QrCodeFragment extends Fragment {

    private Event event;
    private EventViewModel eventViewModel;
    private Button saveQrButton;
    private Button noShowButton;
    private ImageView displayQrImageView;

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
        noShowButton = view.findViewById(R.id.no_show_button);
        displayQrImageView = view.findViewById(R.id.qr_display_imageview);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        try {
            Bitmap qrCodeBitmap = eventViewModel.generateQR(event.getId());
            if (qrCodeBitmap != null) {
                displayQrImageView.setImageBitmap(qrCodeBitmap);
            } else {
                Log.e("QrCodeFragment", "generateQR() returned null");
                displayQrImageView.setImageResource(R.drawable.outline_beach_access_100);
            }
        } catch (WriterException e) {
            Log.e("QrCodeFragment", "Error generating QR code", e);
            displayQrImageView.setImageResource(R.drawable.outline_beach_access_100);
        }
    }

}