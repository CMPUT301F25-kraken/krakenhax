package com.kraken.krakenhax;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents an event in the KrakenHax application.
 * Stores event metadata such as title, categories, timeframe, description, location,
 * and registration radius. Each event may also have associated participant lists
 * (WaitList, WonList, LostList, CancelList) and a poster image.
 *
 * <p>Used by organizers to create and manage events, and by entrants to view and join them.</p>
 */
public class Event implements Parcelable {
    private String title;
    private ArrayList<String> categories;
    private ArrayList<Timestamp> timeframe;
    private String eventDetails;
    private String location;
    private Integer Radius;
    private String poster;
    private ArrayList<Profile> waitList;
    private ArrayList<Profile> lostList;
    private ArrayList<Profile> wonList;
    private ArrayList<Profile> cancelList;
    private ArrayList<Profile> acceptList;
    private String id;
    private int waitListCap;
    private int WinnerNumber;
    private boolean useGeolocation;
    private String orgId;
    private Timestamp dateTime;
    private String qrCodeURL;
    private Timestamp dateCreated;


    /**
     * Constructor for Event class.
     */
    public Event() {
        this.title = "";
        this.categories = new ArrayList<String>();
        this.timeframe = new ArrayList<Timestamp>();
        this.eventDetails = "";
        this.location = "";
        this.Radius = 0;
        this.poster = null;
        this.cancelList = new ArrayList<Profile>();
        this.waitList = new ArrayList<Profile>();
        this.lostList = new ArrayList<Profile>();
        this.wonList = new ArrayList<Profile>();
        this.acceptList = new ArrayList<Profile>();
        this.waitListCap = 0;
        this.WinnerNumber = 0;
        this.useGeolocation = false;
        this.dateCreated = Timestamp.now();
    }

    /**
     * Constructor for Event class with title argument.
     */
    public Event(String title) {
        this.title = title;
        this.categories = new ArrayList<String>();
        this.timeframe = new ArrayList<Timestamp>();
        this.eventDetails = "";
        this.location = "";
        this.Radius = 0;
        this.poster = null;
        this.cancelList = new ArrayList<Profile>();
        this.waitList = new ArrayList<Profile>();
        this.lostList = new ArrayList<Profile>();
        this.wonList = new ArrayList<Profile>();
        this.acceptList = new ArrayList<Profile>();
        this.waitListCap = 0;
        this.WinnerNumber = 0;
        this.useGeolocation = false;
        this.dateCreated = Timestamp.now();
    }

    /** Lightweight constructor for fake/local events */
    public Event(String id,
                 String title,
                 String eventDetails,
                 String location,
                 Integer radius,
                 String poster
                 ) {

        this.id = id;
        this.title = title;
        this.eventDetails = eventDetails != null ? eventDetails : "";
        this.location = location != null ? location : "";
        this.Radius = radius != null ? radius : 0;
        this.poster = poster;

        // initialize collection fields so the object is always safe to use
        this.categories = new ArrayList<>();
        this.timeframe = new ArrayList<>();
        this.cancelList = new ArrayList<Profile>();
        this.waitList = new ArrayList<Profile>();
        this.lostList = new ArrayList<Profile>();
        this.wonList = new ArrayList<Profile>();
        this.acceptList = new ArrayList<Profile>();
        this.waitListCap = 0;
        this.WinnerNumber = 0;
        this.useGeolocation = false;
        this.dateCreated = Timestamp.now();
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
     *        an ArrayList of Timestamp representing the event timeframe
     */
    public ArrayList<Timestamp> getTimeframe() {
        return timeframe;
    }

    /**
     * Sets the timeframe of the event.
     * @param timeframe
     *        an ArrayList of Timestamp representing the event timeframe
     */
    public void setTimeframe(ArrayList<Timestamp> timeframe) {
        this.timeframe = timeframe;
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
     */
    public void setLocation(String location) {
        this.location = location;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the poster of the event.
     * @return
     *        a Bitmap representing the event's poster
     * TODO: Research Bitmaps to see if any other logic needed for getter and setter.
     */
    public String getPoster() {
        return poster;
    }

    /**
     * Sets the poster of the event.
     * @param poster
     *        a Bitmap representing the event's poster
     * TODO: Research Bitmaps to see if any other logic needed for getter and setter.
     */
    public void setPoster(String poster) {
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
     * Retrieves the WaitList associated with this event.
     * The waitlist contains entrants who have registered but are not yet confirmed.
     *
     * @return the ArrayList for this event's WaitList
     */
    public ArrayList<Profile> getWaitList() { return waitList; }

    /**
     * Retrieves the CancelList associated with this event.
     * This list tracks entrants who have cancelled their registration.
     *
     * @return the ArrayList for this event's CancelList
     */

    public ArrayList<Profile> getCancelList() { return cancelList; }

    /**
     * Retrieves the WonList associated with this event.
     * This list contains entrants who have been selected to participate.
     *
     * @return the ArrayList for this event's WonList
     */

    public ArrayList<Profile> getWonList() { return wonList; }

    /**
     * Retrieves the LostList associated with this event.
     * This list stores entrants who were not selected after the lottery draw.
     *
     * @return the ArrayList for this event's LostList
     */

    public ArrayList<Profile> getLostList() { return lostList; }

    /**
     * Retrieves the AcceptList associated with this event.
     * This list stores entrants who accepted their entry after the draw.
     *
     * @return the ArrayList for this event's AcceptList
     */
    public ArrayList<Profile> getAcceptList() { return acceptList; }

    public void addToAcceptList(Profile profile)  {
        if (this.wonList.contains(profile)) {
            this.acceptList.add(profile);
            this.wonList.remove(profile);
        } else {
            Log.d("Event", "Profile must be in wonList to be added to acceptList");
        }
    }

    public void addToCancelList(Profile profile) {
        if (this.wonList.contains(profile)) {
            this.cancelList.add(profile);
            this.wonList.remove(profile);
        } else {
            Log.d("Event", "Profile must be in wonList to be added to cancelList");
        }
    }

    public void addToWaitList(Profile profile) {
        if (this.waitListCap <= 0) {
            this.waitList.add(profile);
        } else {
            if (this.waitList.size() < this.waitListCap && !this.waitList.contains(profile)) {
                this.waitList.add(profile);
            } else {
                throw new IllegalArgumentException("Waitlist is full");
            }
        }
    }

    public void addToWonList(Profile profile) {
        if (wonList.size() >= this.WinnerNumber) {
            throw new IllegalArgumentException("Wonlist is full");
        } else {
            this.wonList.add(profile);
        }
    }

    public void addToLostList(Profile profile) {
        this.lostList.add(profile);
    }

    public void removeFromAcceptList(Profile profile) {
        this.acceptList.remove(profile);
    }

    public void removeFromCancelList(Profile profile) {
        this.cancelList.remove(profile);
    }

    public void removeFromWaitList(Profile profile) {
        this.waitList.remove(profile);
    }

    public void removeFromWonList(Profile profile) {
        this.wonList.remove(profile);
    }

    public void removeFromLostList(Profile profile) {
        this.lostList.remove(profile);
    }

    /**
     * Draws the lottery for the event, selecting a number of winners from the entrant pool.
     * Winners are added to the wonList, and all the losers replace the existing lostList.
     * @param entrantPool
     *        List of Profiles who signed up for the lottery.
     * @param numberOfWinners
     *        The number of winners to be selected from the entrant pool.
     */
    public void drawLottery(ArrayList<Profile> entrantPool, Integer numberOfWinners) {
        ArrayList<Profile> winners = this.wonList;
        ArrayList<Profile> losers = (ArrayList<Profile>) entrantPool.clone();

        ArrayList<Profile> shuffled = (ArrayList<Profile>) entrantPool.clone();
        Collections.shuffle(shuffled);

        int newWinnersAdded = 0;
        int i = 0;
        while (newWinnersAdded < numberOfWinners && i < shuffled.size()) {
            Profile winner = shuffled.get(i);
            if (!winners.contains(winner)) {
                winners.add(winner);
                losers.remove(winner);
                newWinnersAdded++;

                // Add action to users history
                winner.updateHistory(new Action("Won lottery for event", null, this.getId()));
            }
            i++;
        }

        this.lostList = losers;

        // Add actions to users history

        // From organizers perspective
        //String eventOrganizerID = this.getOrgId();
        // get the organizer for the event, will match the id
        //Profile eventOrganzier;
        //eventOrganizer.updateHistory(new Action("Triggered lottery for event", null, this));

        // From the entrants perspective
        for (Profile loser : losers) {
            loser.updateHistory(new Action("Lose lottery for event", null, this.getId()));
        }
    }

    public void setWaitListCap(int cap) {
        this.waitListCap = cap;
    }

    public int getWaitListCap() {
        return this.waitListCap;
    }

    public void setWinnerNumber(int num) {
        this.WinnerNumber = num;
    }

    public int getWinnerNumber() {
        return this.WinnerNumber;
    }

    public void setUseGeolocation(boolean use) {
        this.useGeolocation = use;
    }

    public boolean getUseGeolocation() {
        return this.useGeolocation;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public void setQrCodeURL(String qrCodeURL) {
        this.qrCodeURL = qrCodeURL;
    }

    public String getQrCodeURL() {
        return qrCodeURL;
    }

    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    public Timestamp getDateTime() {
        return dateTime;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param in The Parcel in which the object should be written.
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

