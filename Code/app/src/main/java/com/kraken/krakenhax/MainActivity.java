package com.kraken.krakenhax;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


/**
 * The main and only activity for the application.
 * This activity hosts the navigation component and manages the primary UI container and navigation bars.
 */
public class MainActivity extends AppCompatActivity {
    public NavController navController;
    public BottomNavigationView bottom_navigation_bar;
    public BottomNavigationView admin_navigation_bar;
    public Profile currentUser;
    public boolean loggedIn;
    public boolean admin;
    //public ProfileViewModel profileModel;
    private FirebaseFirestore db;
    //private CollectionReference ProfileRef;

    /**
     * Go through every profile in the firestore database and removes the myWaitList from every
     * profile and replaces it with an empty list called bookmarkedEvents.
     */
    private void removeMyWaitlists() {
        db = FirebaseFirestore.getInstance();
        CollectionReference profileRef = db.collection("Profiles");

        profileRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String docID = document.getId();

                profileRef.document(docID).update(
                        "myWaitlist", com.google.firebase.firestore.FieldValue.delete(),
                        "bookmarkedEvents", new java.util.ArrayList<>()
                ).addOnFailureListener(e -> {
                    android.util.Log.e("Firestore", "Failed to update profile: " + docID, e);
                });
            }
        });
    }

    /**
     * Go through every profile in firebase and add a history array if it does not already have one.
     */
    private void addHistory() {
        db = FirebaseFirestore.getInstance();
        CollectionReference profileRef = db.collection("Profiles");

        profileRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String docID = document.getId();

                profileRef.document(docID).update(
                        "history", new ArrayList<Action>()
                ).addOnFailureListener(e ->
                        Log.e("Firestore", "Failed to update profile: " + docID, e)
                );
            }
        });
    }

    /**
     * Helper function to clean up legacy event data.
     * Goes through all events and adds default values for missing dateTime or orgId fields.
     */
    private void cleanUpLegacyEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("Events");

        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                boolean needsUpdate = false;
                java.util.Map<String, Object> updates = new java.util.HashMap<>();

                // Check if 'dateTime' is missing
                if (!document.contains("dateTime") || document.get("dateTime") == null) {
                    // Create Date object for December 31, 1969
                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.set(1969, java.util.Calendar.DECEMBER, 31, 0, 0, 0);

                    // Convert to Firestore Timestamp
                    com.google.firebase.Timestamp defaultDate = new com.google.firebase.Timestamp(calendar.getTime());

                    updates.put("dateTime", defaultDate);
                    needsUpdate = true;
                }

                // Check if 'orgId' is missing
                if (!document.contains("orgId") || document.get("orgId") == null) {
                    updates.put("orgId", "ytVu305PhzdgB1CBWAQN");
                    needsUpdate = true;
                }

                // Only perform the database write if fields were actually missing
                if (needsUpdate) {
                    eventsRef.document(document.getId()).update(updates)
                            .addOnSuccessListener(aVoid -> android.util.Log.d("LegacyCleanup", "Updated event: " + document.getId()))
                            .addOnFailureListener(e -> android.util.Log.e("LegacyCleanup", "Failed to update event: " + document.getId(), e));
                }
            }
        }).addOnFailureListener(e -> {
            android.util.Log.e("LegacyCleanup", "Error getting events for cleanup", e);
        });
    }

    /**
     * Helper function to ensure all events have a valid timeframe field.
     * If missing or null, sets it to [Jan 1, 2026, Dec 31, 2026].
     */
    private void ensureEventTimeframes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("Events");

        eventsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Check if 'timeframe' is missing, explicitly null, or an empty list
                if (!document.contains("timeframe") || document.get("timeframe") == null || ((java.util.List<?>) document.get("timeframe")).isEmpty()) {

                    // Create the two default dates
                    java.util.Calendar startCal = java.util.Calendar.getInstance();
                    startCal.set(2026, java.util.Calendar.JANUARY, 1, 0, 0, 0);

                    java.util.Calendar endCal = java.util.Calendar.getInstance();
                    endCal.set(2026, java.util.Calendar.DECEMBER, 31, 23, 59, 59);

                    // Convert to Firestore Timestamps
                    com.google.firebase.Timestamp startTs = new com.google.firebase.Timestamp(startCal.getTime());
                    com.google.firebase.Timestamp endTs = new com.google.firebase.Timestamp(endCal.getTime());

                    // Create the list
                    java.util.List<com.google.firebase.Timestamp> defaultTimeframe = new java.util.ArrayList<>();
                    defaultTimeframe.add(startTs);
                    defaultTimeframe.add(endTs);

                    // Update Firestore
                    eventsRef.document(document.getId()).update("timeframe", defaultTimeframe)
                            .addOnSuccessListener(aVoid -> android.util.Log.d("TimeframeCleanup", "Updated event: " + document.getId()))
                            .addOnFailureListener(e -> android.util.Log.e("TimeframeCleanup", "Failed to update event: " + document.getId(), e));
                }
            }
        }).addOnFailureListener(e -> {
            android.util.Log.e("TimeframeCleanup", "Error getting events for cleanup", e);
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
            CharSequence name = "KrakenHax Notifications";
            String description = "Channel for event and system notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;


            NotificationChannel channel = new NotificationChannel("kraken_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up:
     * create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's
     * previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle
     *                           contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Set up splash screen
        androidx.core.splashscreen.SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            createNotificationChannel();
        }

        admin = false;
        loggedIn = false;
        db = FirebaseFirestore.getInstance();
        //ProfileRef = db.collection("Profiles");

        //removeMyWaitlists();
        //cleanUpLegacyEvents();
        //ensureEventTimeframes();
        //addHistory(); // Migration helper: run manually if needed, do not execute on every app start

        // Set up the navigation bar
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        assert navHostFragment != null;
        navController = navHostFragment.getNavController();
        bottom_navigation_bar = findViewById(R.id.bottom_navigation_bar);
        admin_navigation_bar = findViewById(R.id.bottom_navigation_Ad);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            if (destinationId == R.id.LoginFragment || destinationId == R.id.signup || destinationId == R.id.selection_type) {
                bottom_navigation_bar.setVisibility(View.GONE);
                admin_navigation_bar.setVisibility(View.GONE);
            } else if (destinationId == R.id.EventsFragment) {
                NavigationUI.setupWithNavController(bottom_navigation_bar, navController);
                bottom_navigation_bar.setVisibility(View.VISIBLE);
            } else if (destinationId == R.id.adminListFragment) {
                admin_navigation_bar.setVisibility(View.VISIBLE);
                NavigationUI.setupWithNavController(admin_navigation_bar, navController);
                admin = true;
            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as padding to the views. 
            // But not to the bottom so that the nav bar is not too thick
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        handleIntent(getIntent()); //Handle the incoming intent for QR code scanning
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        // Ensure fragments read the latest deep link when the activity is reused
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.ACTION_VIEW.equals(intent.getAction())) {
            android.net.Uri data = intent.getData();
            if (data != null) {
                String eventId = data.getLastPathSegment();
                if (eventId != null) {

                        navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
                        Bundle args = new Bundle();
                        args.putString("eventId", eventId);
                        navController.navigate(R.id.LoginFragment, args);
                    //}
                }
            }
        }
    }

}
