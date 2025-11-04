package com.kraken.krakenhax;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the Profile class.
 * Ensures correct creation, validation, and equality behavior.
 *
 * Author: Amaan
 */
public class ProfileTest {

    private Profile profile;

    @Before
    public void setUp() {
        profile = new Profile("Amaan", "password123", "entrant", "amaan@example.com");
    }

    @Test
    public void testProfileInitialization() {
        assertEquals("Amaan", profile.getUsername());
        assertEquals("password123", profile.getPassword());
        assertEquals("entrant", profile.getType());
        assertEquals("amaan@example.com", profile.getEmail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullUsernameThrowsException() {
        new Profile(null, "123", "entrant", "test@example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPasswordThrowsException() {
        new Profile("User", "", "entrant", "test@example.com");
    }

    @Test
    public void testSettersUpdateValues() {
        profile.setUsername("NewUser");
        profile.setPassword("newpass");
        profile.setEmail("newemail@example.com");
        profile.setType("organizer");

        assertEquals("NewUser", profile.getUsername());
        assertEquals("newpass", profile.getPassword());
        assertEquals("organizer", profile.getType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetUsernameEmptyThrowsException() {
        profile.setUsername("");
    }

    @Test
    public void testEqualityBasedOnUsername() {
        Profile p2 = new Profile("Amaan", "differentPass", "organizer", "other@example.com");
        assertEquals(profile, p2);
    }

    @Test
    public void testHashCodeConsistency() {
        Profile p2 = new Profile("Amaan", "pass", "entrant", "mail@example.com");
        assertEquals(profile.hashCode(), p2.hashCode());
    }

    @Test
    public void testToStringFormat() {
        String str = profile.toString();
        assertTrue(str.contains("Amaan"));
        assertTrue(str.contains("entrant"));
    }
}
