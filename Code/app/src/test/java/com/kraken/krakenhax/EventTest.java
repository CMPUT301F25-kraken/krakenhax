package com.kraken.krakenhax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Event class.
 * Focus on pure Java behaviour: title, categories, and waitlist capacity.
 * Android / Firebase–dependent methods are covered elsewhere.
 */
public class EventTest {

    private Event event;

    @Before
    public void setUp() {
        event = new Event("Test Event");
    }

    @Test
    public void testTitleSetterGetter() {
        event.setTitle("Drift Wars");
        assertEquals("Drift Wars", event.getTitle());
    }

    /**
     * Use a valid category name from Event.availableCategories ("music", "art", etc.).
     * This should succeed without throwing and the category should be removable.
     */
    @Test
    public void testAddAndRemoveCategory_Valid() {
        event.addCategory("music");                // valid category
        assertTrue(event.getCategories().contains("music"));

        event.removeCategory("music");
        assertFalse(event.getCategories().contains("music"));
    }

    /**
     * Adding an invalid category name should throw IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCategoryInvalidThrowsException() {
        event.addCategory("Cars");                 // not in availableCategories, should throw
    }

    /**
     * With unlimited waitlist cap (0), profiles can be added and removed normally.
     */
    @Test
    public void testWaitListAddRemoveUnlimited() {
        event.setWaitListCap(0);                   // unlimited
        Profile p = new Profile("1", "Amaan", "password123",
                "entrant", "amaan@example.com", "0");

        event.addToWaitList(p);
        assertTrue(event.getWaitList().contains(p));

        event.removeFromWaitList(p);
        assertFalse(event.getWaitList().contains(p));
    }

    /**
     * When the waitlist is full, addToWaitList should throw IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWaitListCapacityFullThrowsException() {
        event.setWaitListCap(1);                   // only 1 allowed

        Profile p1 = new Profile("1", "User1", "pass",
                "entrant", "user1@example.com", "0");
        Profile p2 = new Profile("2", "User2", "pass",
                "entrant", "user2@example.com", "0");

        event.addToWaitList(p1);
        // second add should exceed capacity and throw
        event.addToWaitList(p2);
    }
}
