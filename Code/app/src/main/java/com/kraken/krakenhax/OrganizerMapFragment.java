package com.kraken.krakenhax;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrganizerMapFragment extends Fragment {


    private Button backBtn;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            // Get the Event that was passed into this fragment
            Bundle args = getArguments();
            if (args == null) return;

            Event event = args.getParcelable("event");
            if (event == null || event.getWaitList() == null) return;

            FirebaseFirestore db = FirebaseFirestore.getInstance();


            LatLng worldCenter = new LatLng(48.390944, -98.837124);
            googleMap.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(worldCenter, 2f) // 1–3 = very zoomed out
            );

            // Loop over each profile ID in the event waitlist
            for (Profile profile : event.getWaitList()) {
                String profileId = profile.getID();
                db.collection("Profiles").document(profileId)
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (!doc.exists()) return;

                            Double lat = doc.getDouble("latitude");
                            Double lng = doc.getDouble("longitude");

                            if (lat == null || lng == null) return;

                            LatLng pos = new LatLng(lat, lng);

                            googleMap.addMarker(
                                    new MarkerOptions()
                                            .position(pos)
                                            .title(doc.getString("username"))
                            );

                            

                        });
            }
        }

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        backBtn = view.findViewById(R.id.goBack);

        backBtn.setOnClickListener(v-> {
            NavHostFragment.findNavController(this).popBackStack();
        });

    }
}