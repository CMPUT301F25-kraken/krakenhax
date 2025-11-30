package com.kraken.krakenhax;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a user profile within the KrakenHax application.
 * Profiles can belong to entrants, organizers, or administrators.
 * Stores user credentials and contact details.
 *
 * <p>Collaborators: Event, WaitList, Organizer, Administrator</p>
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
    private double latitude;
    private double longitude;
    private ArrayList<String> bookmarkedEvents;
    private String pictureURL;
    private boolean notificationsEnabled = true;
    private Timestamp dateCreated;
    private List<Action> history = new ArrayList<Action>();

    /**
     * Constructs a new user profile with the given details.
     *
     * @param username the user's username
     * @param password the user's password
     * @param type     the role type (entrant, organizer, admin)
     * @param email    the user's email address
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
        this.bookmarkedEvents = new ArrayList<String>();
        this.dateCreated = Timestamp.now();
        //this.history = new ArrayList<Action>();
    }

    /**
     * Constructs a no-argument user profile.
     */
    public Profile() {
        // Empty
    }

    /**
     * Constructs a new instance of the Profile class from a Parcel.
     *
     * @param in the Parcel to read from
     */
    protected Profile(Parcel in) {
        username = in.readString();
        password = in.readString();
        email = in.readString();
        type = in.readString();
        phoneNumber = in.readString();
        ID = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        bookmarkedEvents = in.createStringArrayList();
        pictureURL = in.readString();
        notificationsEnabled = in.readByte() != 0;
        history = in.createTypedArrayList(Action.CREATOR);
    }

    /**
     * Creates a new instance of the Profile class from a Parcel.
     *
     */
    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    /**
     * Gets the user's bookmarked events.
     *
     * @return the user's bookmarked events
     */
    public ArrayList<String> getBookmarkedEvents() {
        return bookmarkedEvents;
    }

    /**
     * Gets the user's latitude.
     *
     * @return the user's latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the user's latitude.
     *
     * @param latitude the new latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the user's longitude.
     *
     * @return the user's longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the user's longitude.
     *
     * @param longitude the new longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Adds an event to the user's bookmarked events.
     *
     * @param eventId the ID of the event to add
     */
    public void addToBookmarkedEvents(String eventId) {
        bookmarkedEvents.add(eventId);
    }

    /**
     * Removes an event from the user's bookmarked events.
     *
     * @param event_id the ID of the event to remove
     */
    public void removeFromBookmarkedEvents(String event_id) {
        this.bookmarkedEvents.remove(event_id);
    }

    /**
     * Returns the user's username.
     *
     * @return the user's username
     */
    // Getters
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's email address.
     *
     * @return the user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's role type.
     *
     * @return the user's role type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the user's phone number.
     *
     * @return the user's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Returns the user's ID.
     *
     * @return the user's ID
     */
    public String getID() {
        return this.ID;
    }
    // Setters

    /**
     * Sets the user's username.
     *
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
     *
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
     *
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
     *
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
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the user's ID.
     *
     * @param ID the new ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Sets the user's profile picture URL.
     *
     * @param pictureURL the new profile picture URL
     */
    public void setPicture(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    /**
     * Returns the user's profile picture URL.
     *
     * @return the user's profile picture URL
     */
    public String getPicture() {
        return this.pictureURL;
    }

    /**
     * Returns the notifications enabled status.
     *
     * @return the notifications enabled status
     */
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    /**
     * Sets the notifications enabled status.
     *
     * @param enabled the new notifications enabled status
     */
    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
    }

    /**
     * Returns the date the profile was created.
     *
     * @return the date the profile was created
     */
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    /**
     * Checks if the given object is equal to this profile.
     *
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
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * Returns a string representation of this profile.
     *
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
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(type);
        dest.writeString(phoneNumber);
        dest.writeString(ID);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeStringList(bookmarkedEvents);
        dest.writeString(pictureURL);
        dest.writeByte((byte) (notificationsEnabled ? 1 : 0));
        dest.writeTypedList(history);
    }

    /**
     * Returns the history list.
     *
     * @return the history list
     */
    public List<Action> getHistory() {
        if (history == null) {
            history = new ArrayList<>();
        }
        return history;
    }

    /**
     * Sets the history list.
     *
     * @param history the history list
     */
    public void setHistory(List<Action> history) {
        this.history = history;
    }

    /**
     * Adds an action to the history list.
     *
     * @param newAction the action to add
     */
    public void updateHistory(Action newAction) {
        if (this.history == null) {
            this.history = new ArrayList<>();
        }
        this.history.add(newAction);
    }
}
