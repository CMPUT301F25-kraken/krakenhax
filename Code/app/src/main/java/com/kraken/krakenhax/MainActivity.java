package com.kraken.krakenhax;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


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
    public ProfileViewModel profileModel;
    private FirebaseFirestore db;
    public boolean admin;
    private CollectionReference ProfileRef;

    /**
     * Called when the activity is first created. This is where you should do all of your normal static set up:
     * create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's
     * previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle
     *                           contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            createNotificationChannel();
        }

        admin = false;
        loggedIn = false;
        db = FirebaseFirestore.getInstance();
        ProfileRef = db.collection("Profiles");

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


}
