package com.kraken.krakenhax;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;


/**
 * Represents an action taken by a user in the app or some event that has occurred to the user.
 * <p>
 * Instances are stored in Firestore and shown in user profile history lists. This class is a
 * plain data object (POJO) with a public no-argument constructor required for Firestore
 * deserialization. It also implements {@link Parcelable} so instances may be passed between
 * Android components via {@link android.content.Intent} extras or saved/restored in bundles.
 * </p>
 * <p>
 * Fields:
 * - {@code action}: human-readable description of the action (for example "joined waitlist").
 * - {@code associatedUserID}: optional ID of another user related to this action.
 * - {@code associatedEventID}: optional ID of an event related to this action.
 * - {@code timestamp}: time when the action occurred (Firebase {@link Timestamp}).
 */
public class Action implements Parcelable {
    // Possible actions: join waitlist, withdraw from waitlist, accept place in event, decline place in event
    // won lottery for event, lost lottery for event, triggered event, removed entrant from a list
    private String action;
    private String associatedUserID;
    private String associatedEventID;
    private Timestamp timestamp;

    /**
     * No-argument public constructor required for Firestore.
     */
    public Action() {
        // Empty constructor needed for Firestore serialization
    }

    /**
     * Constructs an Action with the provided details and sets {@link #timestamp} to now.
     *
     * @param action            human-readable description of the action
     * @param associatedUserID  id of the user associated with the action (may be null)
     * @param associatedEventID id of the event associated with the action (may be null)
     */
    public Action(String action, String associatedUserID, String associatedEventID) {
        this.action = action;
        this.associatedUserID = associatedUserID;
        this.associatedEventID = associatedEventID;
        this.timestamp = Timestamp.now();
    }

    /**
     * Gets the string description of the action.
     *
     * @return the action description
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the string description of the action.
     *
     * @param action the action description to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the ID for the user associated with the action.
     *
     * @return the associated user id, or {@code null} if none
     */
    public String getAssociatedUserID() {
        return associatedUserID;
    }

    /**
     * Sets the ID for the user associated with the action.
     *
     * @param associatedUserID the user id to associate with this action (may be null)
     */
    @SuppressWarnings("unused")
    public void setAssociatedUserID(String associatedUserID) {
        this.associatedUserID = associatedUserID;
    }

    /**
     * Gets the ID for the event associated with the action.
     *
     * @return the associated event id, or {@code null} if none
     */
    public String getAssociatedEventID() {
        return associatedEventID;
    }

    /**
     * Sets the ID for the event associated with the action.
     *
     * @param associatedEventID the event id to associate with this action (may be null)
     */
    @SuppressWarnings("unused")
    public void setAssociatedEventID(String associatedEventID) {
        this.associatedEventID = associatedEventID;
    }

    /**
     * Gets the timestamp for when the action occurred.
     *
     * @return the {@link Timestamp} of the action, or {@code null} if not set
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp for when the action occurred.
     *
     * @param timestamp the {@link Timestamp} to set
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Parcelable Utility Methods

    /**
     * Parcelable creator required to regenerate instances of this class from a Parcel.
     */
    @SuppressWarnings("all")
    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    /**
     * Constructs an Action from a {@link Parcel}.
     * <p>
     * This constructor is used by the {@link #CREATOR} to recreate an instance that was
     * previously written via {@link #writeToParcel(Parcel, int)}.
     *
     * @param in Parcel containing the serialized Action fields
     */
    public Action(Parcel in) {
        action = in.readString();
        associatedUserID = in.readString();
        associatedEventID = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable instance's marshaled
     * representation. For this class no special objects are used.
     *
     * @return a bitmask indicating the set of special object types marshaled by this Parcelable
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object into a {@link Parcel}.
     * <p>
     * The fields are written in the same order they are read in {@link #Action(Parcel)}.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written; passed to
     *              {@link Parcel#writeParcelable(Parcelable, int)} for nested parcelables.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(associatedUserID);
        dest.writeString(associatedEventID);
        dest.writeParcelable(timestamp, flags);
    }

}
