package com.kraken.krakenhax;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class Action implements Parcelable {
    // Possible actions: join waitlist, withdraw from waitlist, accept place in event, decline place in event
    // won lottery for event, lost lottery for event, triggered event, removed entrant from a list
    private String action;
    private String associatedUserID;
    private String associatedEventID;
    private Timestamp timestamp;

    // Firestore requires a public no-argument constructor
    public Action() {
        // Empty constructor needed for Firestore serialization
    }

    public Action(String action, String associatedUserID, String associatedEventID) {
        this.action = action;
        this.associatedUserID = associatedUserID;
        this.associatedEventID = associatedEventID;
        this.timestamp = Timestamp.now();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAssociatedUserID() {
        return associatedUserID;
    }

    public void setAssociatedUserID(String associatedUserID) {
        this.associatedUserID = associatedUserID;
    }

    public String getAssociatedEventID() {
        return associatedEventID;
    }

    public void setAssociatedEventID(String associatedEventID) {
        this.associatedEventID = associatedEventID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    // Parcelable Utility Methods

    public Action(Parcel in) {
        action = in.readString();
        associatedUserID = in.readString();
        associatedEventID = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
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
        dest.writeString(action);
        dest.writeString(associatedUserID);
        dest.writeString(associatedEventID);
        dest.writeParcelable(timestamp, flags);
    }

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
}
