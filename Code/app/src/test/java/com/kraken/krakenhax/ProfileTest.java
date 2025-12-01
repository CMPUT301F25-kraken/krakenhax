package com.kraken.krakenhax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit tests for the Profile class.
 * Ensures correct creation, validation, and equality behavior.
 *
 */
public class ProfileTest {
    private Profile profile;

    /**
     * Sets up a default {@link Profile} instance used by the tests.
     */
    @Before
    public void setUp() {
        profile = new Profile("1", "Amaan", "password123", "entrant", "amaan@example.com", "0");
    }

    /**
     * Verifies that the constructor correctly initializes all profile fields.
     */
    @Test
    public void testProfileInitialization() {
        assertEquals("1", profile.getID());
        assertEquals("Amaan", profile.getUsername());
        assertEquals("password123", profile.getPassword());
        assertEquals("entrant", profile.getType());
        assertEquals("amaan@example.com", profile.getEmail());
        assertEquals("0", profile.getPhoneNumber());
    }

    /**
     * Verifies that creating a profile with a null username throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullUsernameThrowsException() {
        new Profile("2", null, "123", "entrant", "test@example.com", "0");
    }

    /**
     * Verifies that creating a profile with an empty password throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPasswordThrowsException() {
        new Profile("2", "User", "", "entrant", "test@example.com", "0");
    }

    /**
     * Verifies that setter methods correctly update profile values.
     */
    @Test
    public void testSettersUpdateValues() {
        profile.setUsername("NewUser");
        profile.setPassword("newpass");
        profile.setEmail("newemail@example.com");
        profile.setType("organizer");
        profile.setPhoneNumber("1234567890");

        assertEquals("NewUser", profile.getUsername());
        assertEquals("newpass", profile.getPassword());
        assertEquals("organizer", profile.getType());
        assertEquals("newemail@example.com", profile.getEmail());
        assertEquals("1234567890", profile.getPhoneNumber());

    }

    /**
     * Verifies that setting an empty username via the setter throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetUsernameEmptyThrowsException() {
        profile.setUsername("");
    }

    /**
     * Verifies that two profiles with the same username are considered equal.
     */
    @Test
    public void testEqualityBasedOnUsername() {
        Profile p2 = new Profile("1", "Amaan", "differentPass", "organizer", "other@example.com", "0");
        assertEquals(profile, p2);
    }

    /**
     * Verifies that equal profiles produce the same hash code.
     */
    @Test
    public void testHashCodeConsistency() {
        Profile p2 = new Profile("1", "Amaan", "pass", "entrant", "mail@example.com", "0");
        assertEquals(profile.hashCode(), p2.hashCode());
    }

    /**
     * Verifies that the string representation contains key profile information.
     */
    @Test
    public void testToStringFormat() {
        String str = profile.toString();
        assertTrue(str.contains("Amaan"));
        assertTrue(str.contains("entrant"));
    }

}
