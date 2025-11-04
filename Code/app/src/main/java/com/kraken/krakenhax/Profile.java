package com.kraken.krakenhax;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a user profile within the KrakenHax application.
 * Profiles can belong to entrants, organizers, or administrators.
 * Stores user credentials and contact details.
 *
 * <p>Collaborators: Event, WaitList, Organizer, Administrator</p>
 *
 * @author Amaan
 * @version 1.1
 */
public class Profile implements Serializable {

    private String username;
    private String password;
    private String email;
    private String type; // e.g., "entrant", "organizer", "admin"

    /**
     * Constructs a new user profile with the given details.
     * @param username the user's username
     * @param password the user's password
     * @param type the role type (entrant, organizer, admin)
     * @param email the user's email address
     * @throws IllegalArgumentException if any parameter is null or empty
     */
    public Profile(String username, String password, String type, String email) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be null or empty.");
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be null or empty.");
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be null or empty.");
        if (type == null || type.trim().isEmpty())
            throw new IllegalArgumentException("User type cannot be null or empty.");

        this.username = username;
        this.password = password;
        this.type = type;
        this.email = email;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getType() { return type; }

    // Setters
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username;
    }

    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be empty.");
        this.email = email;
    }

    public void setType(String type) {
        if (type == null || type.trim().isEmpty())
            throw new IllegalArgumentException("Type cannot be empty.");
        this.type = type;
    }

    // Utility Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;
        Profile profile = (Profile) o;
        return username.equals(profile.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return "Profile{" +
                "username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
