package com.kraken.krakenhax;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for Action model.
 */
public class ActionTest {

    @Test
    public void testActionToStringIsNotNullOrEmpty() {
        Action a = new Action("Join waitlist", "n/a", "event123");

        String s = a.toString();

        assertNotNull(s);
        assertFalse(s.isEmpty());
    }
}
