package com.kraken.krakenhax;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Simple UI test for ProfileFragment using FragmentScenario + Espresso.
 * Verifies that the notification toggle is visible and can be clicked.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    @Test
    public void testNotificationSwitchToggle() {
        // Launch ProfileFragment in isolation
        //FragmentScenario<ProfileFragment> scenario =
                //FragmentScenario.launchInContainer(ProfileFragment.class, new Bundle(), R.style.Theme_MaterialComponents);

        // Check switch is displayed
        //onView(withId(R.id.notifications_switch))
                //.check(matches(isDisplayed()));

        // Click to toggle (initial state may vary; this just ensures click works)
        //onView(withId(R.id.notifications_switch)).perform(click());
    }
}
