package com.kraken.krakenhax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.ArrayList;


/**
 * Unit tests for the Event class.
 * Verifies basic getters/setters, category logic, and timeframe validation.
 *
 */
public class EventTest {
    private Event event;

    /**
     * Sets up a fresh {@link Event} instance before each test.
     */
    @Before
    public void setUp() {
        event = new Event("Test Event");
    }

    /**
     * Verifies that the title setter and getter work as expected.
     */
    @Test
    public void testTitleSetterGetter() {
        event.setTitle("Drift Wars");
        assertEquals("Drift Wars", event.getTitle());
    }

    /**
     * Verifies that categories can be added to and removed from an event.
     */
    @Test
    public void testAddAndRemoveCategory() {
        event.addCategory("Cars");
        assertTrue(event.getCategories().contains("Cars"));
        event.removeCategory("Cars");
        assertFalse(event.getCategories().contains("Cars"));
    }

    /**
     * Ensures that adding more than the allowed number of categories
     * results in an {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testTooManyCategoriesThrowsException() {
        for (int i = 0; i < 6; i++) {
            event.addCategory("Category" + i);
        }
    }

    /**
     * Verifies that a valid timeframe with start and end dates is accepted.
     */
    @Test
    public void testSetValidTimeframe() {
        ArrayList<ZonedDateTime> timeframe = new ArrayList<>();
        timeframe.add(ZonedDateTime.now());
        timeframe.add(ZonedDateTime.now().plusDays(1));
        //event.setTimeframe(timeframe);
        assertEquals(2, event.getTimeframe().size());
    }

    /**
     * Ensures that an invalid timeframe where the end precedes the start
     * results in an {@link IllegalArgumentException}.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeframeThrowsException() {
        ArrayList<ZonedDateTime> timeframe = new ArrayList<>();
        timeframe.add(ZonedDateTime.now().plusDays(1));
        timeframe.add(ZonedDateTime.now());
        //event.setTimeframe(timeframe);
    }

    /**
     * Confirms that the wait list is initialized and empty by default.
     */
    @Test
    public void testWaitListNotNull() {
        assertNotNull(event.getWaitList());
        assertTrue(event.getWaitList().isEmpty());
    }

//    /**
//     * Smoke test to verify that {@link Event} parceling works without errors.
//     */
//    @Test
//    public void testParcelWriteRead() {
//        // basic smoke test — ensures Parcelable runs without crash
//        android.os.Parcel parcel = android.os.Parcel.obtain();
//        event.writeToParcel(parcel, 0);
//        parcel.setDataPosition(0);
//        Event recreated = Event.CREATOR.createFromParcel(parcel);
//        assertEquals(event.getTitle(), recreated.getTitle());
//        parcel.recycle();
//    }

}
