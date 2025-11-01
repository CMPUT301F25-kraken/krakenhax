package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
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


public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference ProfileRef;
    public NavController navController;
    public BottomNavigationView bottom_navigation_bar;
    public Profile currentUser;
    public boolean loggedIn;
    public ProfileViewModel profileModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        loggedIn = false;
        db = FirebaseFirestore.getInstance();
        ProfileRef = db.collection("Users");

        // Set up the navigation bar
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottom_navigation_bar = findViewById(R.id.bottom_navigation_bar);
        NavigationUI.setupWithNavController(bottom_navigation_bar, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            if (destinationId == R.id.LoginFragment || destinationId == R.id.signup || destinationId == R.id.selection_type) {
                bottom_navigation_bar.setVisibility(View.GONE);
            } else {
                bottom_navigation_bar.setVisibility(View.VISIBLE);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


}