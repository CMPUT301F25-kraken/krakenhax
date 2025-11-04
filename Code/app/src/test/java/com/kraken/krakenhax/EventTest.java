package com.kraken.krakenhax;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.time.ZonedDateTime;
import static org.junit.Assert.*;

/**
 * Unit tests for the Event class.
 * Verifies basic getters/setters, category logic, and timeframe validation.
 *
 * Author: Amaan
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

    @Test
    public void testAddAndRemoveCategory() {
        event.addCategory("Cars");
        assertTrue(event.getCategories().contains("Cars"));
        event.removeCategory("Cars");
        assertFalse(event.getCategories().contains("Cars"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTooManyCategoriesThrowsException() {
        for (int i = 0; i < 6; i++) {
            event.addCategory("Category" + i);
        }
    }

    @Test
    public void testSetValidTimeframe() {
        ArrayList<ZonedDateTime> timeframe = new ArrayList<>();
        timeframe.add(ZonedDateTime.now());
        timeframe.add(ZonedDateTime.now().plusDays(1));
        event.setTimeframe(timeframe);
        assertEquals(2, event.getTimeframe().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimeframeThrowsException() {
        ArrayList<ZonedDateTime> timeframe = new ArrayList<>();
        timeframe.add(ZonedDateTime.now().plusDays(1));
        timeframe.add(ZonedDateTime.now());
        event.setTimeframe(timeframe);
    }

    @Test
    public void testWaitListNotNull() {
        assertNotNull(event.getWaitList());
        assertTrue(event.getWaitList().isEmpty());
    }

    /**
    @Test

    public void testParcelWriteRead() {
        // basic smoke test — ensures Parcelable runs without crash
        android.os.Parcel parcel = android.os.Parcel.obtain();
        event.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Event recreated = Event.CREATOR.createFromParcel(parcel);
        assertEquals(event.getTitle(), recreated.getTitle());
        parcel.recycle();
    }
    */
}
