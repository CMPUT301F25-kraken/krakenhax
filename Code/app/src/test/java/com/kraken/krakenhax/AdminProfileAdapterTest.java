package com.kraken.krakenhax;

import static org.junit.Assert.*; // Use static imports from JUnit

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import androidx.appcompat.R;
//import org.robolectric.annotation.DoNotInstrument;

import java.util.ArrayList;

@Config(manifest="src/main/AndroidManifest.xml", sdk = 33)
@RunWith(RobolectricTestRunner.class)
public class AdminProfileAdapterTest {

    private AdminProfileAdapter adapter;
    private ArrayList<Profile> profileList;
    private Context context;
    private Profile testProfile1;
    private Profile testProfile2;

    // Use a @Before method to set up common test objects
    @Before
    public void setUp() {
        // Get context from Robolectric
        context = ApplicationProvider.getApplicationContext();

        // Create a fresh list and profiles for each test
        profileList = new ArrayList<>();
        testProfile1 = new Profile("TestUser1", "test1@email.com", "pass", "Organizer", "Bio1", "0");
        testProfile2 = new Profile("TestUser2", "test2@email.com", "pass", "Entrant", "Bio2", "1");

        profileList.add(testProfile1);
        profileList.add(testProfile2);

        // Initialize the adapter
        adapter = new AdminProfileAdapter(context, profileList);

    }

    /**
     * Tests that the adapter correctly reports the number of items in the list.
     */
    @Test
    public void testGetCount() {
        assertEquals("Adapter count should match the list size", 2, adapter.getCount());
    }

    /**
     * Tests that the adapter returns the correct Profile object for a given position.
     */
    @Test
    public void testGetItem() {
        // Check that getItem() returns the correct profile object
        assertSame("Item at position 0 should be testProfile1", testProfile1, adapter.getItem(0));
        assertSame("Item at position 1 should be testProfile2", testProfile2, adapter.getItem(1));
    }

    /**
     * This is the most important test.
     * It checks if the adapter correctly populates the row's views with data from the Profile object.
     */
    //@Test
    //public void testGetView_PopulatesViewsCorrectly() {
        // Get the inflated view for the first row (position 0)
        // We pass null for parent because we are only testing the view's content, not its layout params.
    //    LinearLayout parent = new LinearLayout(context);
     //   View view;
     //   view = adapter.getView(0, null, parent);

        // Assert that the view is not null
     //   assertNotNull("The inflated view should not be null", view);

        // Find the TextViews and ImageView inside the inflated view
        // These IDs must match the IDs in your list_content_profile.xml
     //   TextView usernameTextView = view.findViewById(com.kraken.krakenhax.R.id.UsernameDisplay);
      //  TextView emailTextView = view.findViewById(com.kraken.krakenhax.R.id.EmailDisplay);
     //   ImageView profilePicImageView = view.findViewById(com.kraken.krakenhax.R.id.profilePic);

        // Assert that the sub-views were found
      //  assertNotNull("Username TextView should be found in the layout", usernameTextView);
      //  assertNotNull("Email TextView should be found in the layout", emailTextView);
       // assertNotNull("Profile pic ImageView should be found in the layout", profilePicImageView);

        // Assert that the TextViews are populated with the correct data from testProfile1
      //  assertEquals("Username should be correctly set", testProfile1.getUsername(), usernameTextView.getText().toString());
      //  assertEquals("Email should be correctly set", testProfile1.getEmail(), emailTextView.getText().toString());
    //}


}

