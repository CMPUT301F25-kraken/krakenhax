package com.kraken.krakenhax;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Represents a user profile within the KrakenHax application.
 * Profiles can belong to entrants, organizers, or administrators.
 * Stores user credentials and contact details.
 *
 * <p>Collaborators: Event, WaitList, Organizer, Administrator</p>
 *
 *
 * @version 1.2
 */
public class Profile implements Parcelable {

    private String username;
    private String password;
    private String email;
    private String type; // e.g., "entrant", "organizer", "admin"
    private String phoneNumber;
    private String ID;
    private String pictureURL;
    private boolean notificationsEnabled = true;
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean enabled) { this.notificationsEnabled = enabled; }

    /**
     * Constructs a new user profile with the given details.
     * @param username the user's username
     * @param password the user's password
     * @param type the role type (entrant, organizer, admin)
     * @param email the user's email address
     * @throws IllegalArgumentException if any parameter is null or empty
     */

    public Profile(String ID, String username, String password, String type, String email, String phoneNumber) {
        if (ID == null || ID.trim().isEmpty())
            throw new IllegalArgumentException("ID cannot be null or empty.");
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
        this.phoneNumber = phoneNumber;
        this.ID = ID;
    }
    /**
     * Constructs a no-argument user profile.
     */
    public Profile(){
        // Empty
    }

    /**
     * Returns the user's username.
     * @return the user's username
     */
    // Getters
    public String getUsername() { return username; }

    /**
     * Returns the user's password.
     * @return the user's password
     */
    public String getPassword() { return password; }
    /**
     * Returns the user's email address.
     * @return the user's email address
     */
    public String getEmail() { return email; }
    /**
     * Returns the user's role type.
     * @return the user's role type
     */
    public String getType() { return type; }
    /**
     * Returns the user's phone number.
     * @return the user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    /**
     * Returns the user's ID.
     * @return the user's ID
     */
    public String getID() {
        return this.ID;
    }
    // Setters
    /**
     * Sets the user's username.
     * @param username the new username
     * @throws IllegalArgumentException if the username is null or empty
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty.");
        this.username = username;
    }
    /**
     * Sets the user's password.
     * @param password the new password
     * @throws IllegalArgumentException if the password is null or empty
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = password;
    }
    /**
     * Sets the user's email address.
     * @param email the new email address
     * @throws IllegalArgumentException if the email is null or empty
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("Email cannot be empty.");
        this.email = email;
    }
    /**
     * Sets the user's role type.
     * @param type the new role type
     * @throws IllegalArgumentException if the type is null or empty
     */
    public void setType(String type) {
        if (type == null || type.trim().isEmpty())
            throw new IllegalArgumentException("Type cannot be empty.");
        this.type = type;
    }
    /**
     * Sets the user's phone number.
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Sets the user's ID.
     * @param ID the new ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    public void setPicture(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getPicture() {
        return this.pictureURL;
    }


    /**
     * Checks if the given object is equal to this profile.
     * @param o
     * @return true if the objects are equal, false otherwise
     */
    // Utility Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        // Objects.equals handles nulls correctly for both objects
        return Objects.equals(ID, profile.ID) &&
                Objects.equals(username, profile.username);
    }
    /**
     * Generates a hash code for this profile.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
    /**
     * Returns a string representation of this profile.
     * @return the string representation
     */
    @NonNull
    @Override
    public String toString() {
        return "Profile{" +
                "username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest
     *         The Parcel in which the object should be written.
     * @param flags
     *         Additional flags about how the object should be written.
     *         May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {

    }

}
