package com.kraken.krakenhax;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;

/**
 * UI test for EventFragment.
 * Verifies the title and signup button are visible when the fragment is launched.
 */
@RunWith(AndroidJUnit4.class)
public class EventFragmentTest {

    @Test
    public void testEventDetailsDisplayed() {
        // Build a fake Event object
        Event event = new Event("UI Test Event");
        event.setId("event_ui_test");
        event.setEventDetails("This is a test event.");
        event.setLocation("Test City");
        event.setDateTime(new Timestamp(new Date()));

        // Optionally set timeframe if needed
        ArrayList<Timestamp> timeframe = new ArrayList<>();
        timeframe.add(new Timestamp(new Date()));
        timeframe.add(new Timestamp(new Date(System.currentTimeMillis() + 3600_000)));
        event.setTimeframe(timeframe);

        // Put Event into Bundle
        Bundle args = new Bundle();
        args.putParcelable("event", event);

        //FragmentScenario<EventFragment> scenario =
                //FragmentScenario.launchInContainer(EventFragment.class, args, R.style.Theme_MaterialComponents);

        // Check the event title is displayed
        onView(withId(R.id.tv_event_name))
                .check(matches(isDisplayed()))
                .check(matches(withText("UI Test Event")));

        // Check the signup button exists
        onView(withId(R.id.button_signup))
                .check(matches(isDisplayed()));
    }
}
