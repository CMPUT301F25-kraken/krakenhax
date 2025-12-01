package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Fragment that shows a Google Map with markers for each profile on an event's waitlist.
 * <p>
 * The fragment reads an Event passed in the fragment arguments under the key "event",
 * fetches profile locations from Firestore, and displays markers on the map. A back
 * button allows returning to the previous screen.
 */
public class OrganizerMapFragment extends Fragment {
    // Callback invoked when the GoogleMap is ready. Adds markers for each waitlisted profile.
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Called when the map is ready to be used.
         *
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         *
         * @param googleMap the GoogleMap instance that is ready
         */
        public void onMapReady(@NonNull GoogleMap googleMap) {
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

    /**
     * Inflate the fragment layout containing the map.
     *
     * @param inflater           LayoutInflater to inflate views
     * @param container          Optional parent view that this fragment's UI should be attached to
     * @param savedInstanceState Saved state bundle
     * @return the root view for the fragment's UI
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_map, container, false);
    }

    /**
     * Called after the fragment's view has been created. Wires the map fragment and back button.
     *
     * @param view               The fragment's root view
     * @param savedInstanceState Saved state bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        // assign previously-declared field
        // Button that navigates back when clicked.
        Button backBtn = view.findViewById(R.id.goBack);

        backBtn.setOnClickListener(v ->
                NavHostFragment.findNavController(this).popBackStack()
        );
    }

}
