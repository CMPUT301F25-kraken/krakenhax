package com.kraken.krakenhax;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        TextView tv_hello = findViewById(R.id.tv_hello);
        BottomNavigationView bottom_navigation_bar = findViewById(R.id.bottom_navigation_bar);

        bottom_navigation_bar.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navbar_events) {
                tv_hello.setText(R.string.events);
            } else if (item.getItemId() == R.id.navbar_my_events) {
                tv_hello.setText(R.string.my_events);
            } else if (item.getItemId() == R.id.navbar_profile) {
                tv_hello.setText(R.string.profile);
            } else {
                return false;
            }
            return true;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}