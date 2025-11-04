package com.kraken.krakenhax;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;

/**
 * Represents an event in the KrakenHax application.
 * Stores event metadata such as title, categories, timeframe, description, location,
 * and registration radius. Each event may also have associated participant lists
 * (WaitList, WonList, LostList, CancelList) and a poster image.
 *
 * <p>Used by organizers to create and manage events, and by entrants to view and join them.</p>
 */

public class Event implements Parcelable {
    private String title; //Done
    private ArrayList<String> categories; //Done
    private ArrayList<ZonedDateTime> timeframe; //Done
    private String eventDetails; //Done
    private String location; //Done
    private Integer Radius; //Done
    private Bitmap poster; //*
    private WaitList waitList;
    private CancelList cancelList;
    private WonList wonList;
    private LostList lostList;


    /**
     * Constructor for Event class.
     */
    public Event() {
        this.title = "";
        this.categories = new ArrayList<String>();
        this.timeframe = new ArrayList<ZonedDateTime>();
        this.eventDetails = "";
        this.location = "";
        this.Radius = 0;
        this.poster = null;
        this.cancelList = new CancelList();
        this.waitList = new WaitList();
        this.lostList = new LostList();
        this.wonList = new WonList();
    }

    /**
     * Constructor for Event class with title argument.
     */
    public Event(String title) {
        this.title = title;
        this.categories = new ArrayList<String>();
        this.timeframe = new ArrayList<ZonedDateTime>();
        this.eventDetails = "";
        this.location = "";
        this.Radius = 0;
        this.poster = null;
        this.waitList = new WaitList();
        this.cancelList = new CancelList();
        this.wonList = new WonList();
        this.lostList = new LostList();

    }

    /**
     * Returns the title of the event.
     * @return
     *        a String representing the event title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the event.
     * @param title
     *        a String representing the event title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns categories assigned to the event
     * @return
     *        an ArrayList of Strings representing the event categories
     */
    public ArrayList<String> getCategories() {
        return categories;
    }

    /**
     * Sets the categories assigned to the event.
     * @param categories
     *        an ArrayList of Strings representing the event categories
     * @throws IllegalArgumentException
     *        if the categories ArrayList is more than 5
     * TODO: decide whether 5 categories is enough, or too many??
     */
    public void setCategories(ArrayList<String> categories) {
        if (categories.size() <= 5) {
            this.categories = categories;
        }
        else {
            throw new IllegalArgumentException("Categories cannot be more than 5");
        }
    }

    /**
     * Adds a category to the event.
     * @param category
     *        a String representing the category to be added
     * @throws IllegalArgumentException
     *        if the categories ArrayList is more than 5
     *        TODO: decide whether 5 categories is enough, or too many??
     */
    public void addCategory(String category) {
        if (categories.size() < 5) {
            categories.add(category);
        }
        else {
            throw new IllegalArgumentException("Categories cannot be more than 5");
        }
    }

    /**
     * Removes a category from the event.
     * @param category
     *        a String representing the category to be removed
     * @throws IllegalArgumentException
     *        if the categories ArrayList is empty
     *        if the category does not exist in the ArrayList
     *
     */
    public void removeCategory(String category) {
        if (!categories.contains(category)) {
            throw new IllegalArgumentException("Category does not exist");
        } else if (categories.isEmpty()) {
            throw new IllegalArgumentException("Categories cannot be empty");
        } else {
            categories.remove(category);
        }
    }


    /**
     * Returns the timeframe of the event.
     * @return
     *        an ArrayList of ZonedDateTime representing the event timeframe
     */
    public ArrayList<ZonedDateTime> getTimeframe() {
        return timeframe;
    }


    /**
     * Sets the timeframe of the event.
     * @param timeframe
     *        an ArrayList of ZonedDateTime representing the event timeframe
     * @throws IllegalArgumentException
     *        if the timeframe ArrayList is not of size 2
     *        if the start time is after the end time
     */
    public void setTimeframe(ArrayList<ZonedDateTime> timeframe) {
        if (timeframe.size() != 2) {
            throw new IllegalArgumentException("Timeframe only have start and end fields");
        } else if (timeframe.get(0).isAfter(timeframe.get(1))) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        } else {
            this.timeframe = timeframe;
        }
    }


    /**
     * Returns the details of the event.
     * @return
     *        a String representing the event details
     */
    public String getEventDetails() {
        return eventDetails;
    }


    /**
     * Sets the details of the event.
     * @param eventDetails
     *        a String representing the event details
     */
    public void setEventDetails(String eventDetails) {
        this.eventDetails = eventDetails;
    }


    /**
     * Returns the address of the event.
     * @return
     *        a String representing the event's address
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the address of the event.
     * @param location
     *        a String representing the event's address
     * @throws IllegalArgumentException
     *        if the location does not contain a number in the address
     */
    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        } else {
            this.location = location;
        }
    }

    /**
     * Returns the radius of the event.
     * @return
     *        an Integer representing the event's radius
     */
    public Integer getRadius() {
        return Radius;
    }

    /**
     * Sets the radius of the event.
     * @param radius
     *        an Integer representing the event's radius
     */
    public void setRadius(Integer radius) {
        Radius = radius;
    }

    /**
     * Returns the poster of the event.
     * @return
     *        a Bitmap representing the event's poster
     * TODO: Research Bitmaps to see if any other logic needed for getter and setter.
     */
    public Bitmap getPoster() {
        return poster;
    }

    /**
     * Sets the poster of the event.
     * @param poster
     *        a Bitmap representing the event's poster
     * TODO: Research Bitmaps to see if any other logic needed for getter and setter.
     */
    public void setPoster(Bitmap poster) {
        this.poster = poster;
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
     * Retrieves the {@link WaitList} associated with this event.
     * The waitlist contains entrants who have registered but are not yet confirmed.
     *
     * @return the WaitList instance for this event
     */
    public WaitList getWaitList() { return waitList; }

    /**
     * Retrieves the {@link CancelList} associated with this event.
     * This list tracks entrants who have cancelled their registration.
     *
     * @return the CancelList instance for this event
     */

    public CancelList getCancelList() { return cancelList; }

    /**
     * Retrieves the {@link WonList} associated with this event.
     * This list contains entrants who have been selected to participate.
     *
     * @return the WonList instance for this event
     */

    public WonList getWonList() { return wonList; }

    /**
     * Retrieves the {@link LostList} associated with this event.
     * This list stores entrants who were not selected after the lottery draw.
     *
     * @return the LostList instance for this event
     */

    public LostList getLostList() { return lostList; }



    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */

    protected Event(Parcel in) {
        title = in.readString();
        categories = in.createStringArrayList();
        eventDetails = in.readString();
        location = in.readString();
        if (in.readByte() == 0) {
            Radius = null;
        } else {
            Radius = in.readInt();
        }
    }

    /**
     * A {@link Parcelable.Creator} that generates instances of {@link Event} from a {@link Parcel}.
     * <p>This is required for passing {@code Event} objects between Android components,
     * such as when navigating between fragments or activities.</p>
     */

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    /**
     * Writes the {@link Event} object's data into a {@link Parcel}, allowing it to be
     * serialized and passed between Android components.
     *
     * <p>Note: The {@link Bitmap} poster is excluded from parceling for efficiency.</p>
     *
     * @param dest  The Parcel object in which the Event data should be written.
     * @param flags Additional flags about how the object should be written.
     */

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeStringList(categories);
        dest.writeString(eventDetails);
        dest.writeString(location);
        if (Radius == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Radius);
        }
    }
}
