package com.kraken.krakenhax;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

/**
 * Tests for the WaitList class.
 * Verifies adding, removing, clearing, and notification logic.
 *
 * Author: Amaan
 */
public class WaitListTest {

    private WaitList waitList;
    private Profile entrant1;
    private Profile entrant2;

    @Before
    public void setUp() {
        waitList = new WaitList();
        entrant1 = new Profile("User1", "pass123", "entrant", "user1@example.com");
        entrant2 = new Profile("User2", "pass456", "entrant", "user2@example.com");
    }

    @Test
    public void testAddEntrant() {
        waitList.addEntrant(entrant1);
        assertTrue(waitList.getEntrants().contains(entrant1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullEntrantThrowsException() {
        waitList.addEntrant(null);
    }

    @Test
    public void testRemoveEntrant() {
        waitList.addEntrant(entrant1);
        waitList.removeEntrant(entrant1);
        assertFalse(waitList.getEntrants().contains(entrant1));
    }

    @Test
    public void testClearEntrants() {
        waitList.addEntrant(entrant1);
        waitList.addEntrant(entrant2);
        waitList.clearEntrants();
        assertTrue(waitList.isEmpty());
    }

    @Test
    public void testNotifyEntrantsDoesNotCrash() {
        waitList.addEntrant(entrant1);
        NotifyUser notifier = new NotifyUser();
        waitList.notifyEntrants(notifier, "Test message");
        // If no exceptions thrown, pass
    }
}
