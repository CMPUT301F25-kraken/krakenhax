package com.kraken.krakenhax;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

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
    public String poster;
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

    /**
     * Lightweight constructor for creating fake/local events.
     *
     * @param id
     *        a String representing the unique identifier for the event
     * @param title
     *        a String representing the event title
     * @param eventDetails
     *        a String representing the event details (defaults to empty string)
     * @param location
     *        a String representing the event location (defaults to empty string)
     * @param radius
     *        an Integer representing the event's radius (defaults to 0)
     * @param poster
     *        a String representing the URL or path to the event's poster image
     */
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

    /**
     * Returns the unique identifier of the event.
     * @return
     *        a String representing the event's unique ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the event.
     * @param id
     *        a String representing the event's unique ID
     */
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

    /**
     * Adds a profile to the AcceptList if they are currently in the WonList.
     * When a profile accepts their invitation, they are moved from the WonList to the AcceptList.
     *
     * @param profile
     *        the Profile to be added to the AcceptList
     */
    public void addToAcceptList(Profile profile)  {
        if (this.wonList.contains(profile)) {
            this.acceptList.add(profile);
            this.wonList.remove(profile);
        } else {
            Log.d("Event", "Profile must be in wonList to be added to acceptList");
        }
    }

    /**
     * Adds a profile to the CancelList if they are currently in the WonList.
     * When a winner cancels, they are moved from WonList to CancelList and a replacement
     * winner is drawn from the LostList.
     *
     * @param profile
     *        the Profile to be added to the CancelList
     */
    public void addToCancelList(Profile profile) {
        if (this.wonList.contains(profile)) {
            this.cancelList.add(profile);
            this.wonList.remove(profile);

            // Draw one more winner to replace the cancelled profile
            drawReplacementWinner(1);
        } else {
            Log.d("Event", "Profile must be in wonList to be added to cancelList");
        }
    }

    /**
     * Selects replacement winner(s) from the lostList when a winner cancels.
     * Moves the selected profile(s) from lostList to wonList and updates their history.
     * Does NOT update organizer or other profiles' histories.
     *
     * @param count
     *        the number of replacement winners to select
     */
    private void drawReplacementWinner(int count) {
        if (lostList == null || lostList.isEmpty() || count <= 0) {
            return;
        }
        // Shuffle lostList to randomize selection
        ArrayList<Profile> shuffledLostList = new ArrayList<>(lostList);
        Collections.shuffle(shuffledLostList);
        int winnersToDraw = Math.min(count, shuffledLostList.size());
        for (int i = 0; i < winnersToDraw; i++) {
            Profile replacement = shuffledLostList.get(i);
            wonList.add(replacement);
            lostList.remove(replacement);
            // Update replacement winner's history
            replacement.addHistory("Selected as replacement winner for event: " + this.title);
        }
    }

    /**
     * Adds a profile to the WaitList for this event.
     * If a waitlist capacity is set and the waitlist is full, an exception is thrown.
     * Duplicate profiles are not allowed.
     *
     * @param profile
     *        the Profile to be added to the WaitList
     * @throws IllegalArgumentException
     *        if the waitlist is full or the profile is already on the waitlist
     */
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

    /**
     * Removes a profile from the WaitList.
     *
     * @param profile
     *        the Profile to be removed from the WaitList
     */
    public void removeFromWaitList(Profile profile) {
        this.waitList.remove(profile);
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                waitList.remove(winner);
                newWinnersAdded++;

                // Add action to users history
                winner.updateHistory(new Action("Won lottery for event", null, this.getId()));
                // Save to firestore
                db.collection("Profiles").document(winner.getID()).set(winner)
                        .addOnFailureListener(e -> Log.e("drawLottery", "Failed to save winner history", e));

            }
            i++;
        }

        this.lostList = losers;

        // Add actions to users history

        // From organizers perspective
        String eventID = this.getId();
        // get the organizer for the event, will match the id
        this.lookupOrganizer(new Event.OrganizerCallback() {
            @Override
            public void onOrganizerFound(Profile organizer) {
                if (organizer != null) {
                    organizer.updateHistory(new Action("Triggered lottery for event", null, eventID));
                    // Save to firestore
                    db.collection("Profiles").document(organizer.getID()).set(organizer)
                            .addOnFailureListener(e -> Log.e("drawLottery", "Failed to save organizer history", e));
                }
            }

            /**
             * If there is an error.
             */
            @Override
            public void onError(Exception e) {
                Log.e("lookupOrganizer", "Error: Cannot lookup organizer", e);
            }
        });

        // From the entrants perspective
        for (Profile loser : losers) {
            loser.updateHistory(new Action("Lose lottery for event", null, this.getId()));
            db.collection("Profiles").document(loser.getID()).update("history", loser.getHistory())
                    .addOnFailureListener(e -> Log.e("drawLottery", "Failed to save loser history", e));
        }
    }

    /**
     * Sets the maximum capacity for the waitlist.
     * A value of 0 or less indicates no capacity limit.
     *
     * @param cap
     *        an integer representing the maximum number of entrants allowed on the waitlist
     */
    public void setWaitListCap(int cap) {
        this.waitListCap = cap;
    }

    /**
     * Returns the maximum capacity for the waitlist.
     *
     * @return an integer representing the waitlist capacity (0 or less means unlimited)
     */
    public int getWaitListCap() {
        return this.waitListCap;
    }

    /**
     * Sets the number of winners to be selected in the lottery draw.
     *
     * @param num
     *        an integer representing the number of winners to select
     */
    public void setWinnerNumber(int num) {
        this.WinnerNumber = num;
    }

    /**
     * Returns the number of winners to be selected in the lottery draw.
     *
     * @return an integer representing the number of winners
     */
    public int getWinnerNumber() {
        return this.WinnerNumber;
    }

    /**
     * Sets whether geolocation verification is required for this event.
     *
     * @param use
     *        a boolean indicating if geolocation should be used (true) or not (false)
     */
    public void setUseGeolocation(boolean use) {
        this.useGeolocation = use;
    }

    /**
     * Returns whether geolocation verification is required for this event.
     *
     * @return a boolean indicating if geolocation is used
     */
    public boolean getUseGeolocation() {
        return this.useGeolocation;
    }

    /**
     * Sets the organizer ID for this event.
     *
     * @param orgId
     *        a String representing the unique ID of the organizer
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Returns the organizer ID for this event.
     *
     * @return a String representing the organizer's unique ID
     */
    public String getOrgId() {
        return this.orgId;
    }

    /**
     * Sets the QR code URL for this event.
     *
     * @param qrCodeURL
     *        a String representing the URL or path to the QR code image
     */
    public void setQrCodeURL(String qrCodeURL) {
        this.qrCodeURL = qrCodeURL;
    }

    /**
     * Returns the QR code URL for this event.
     *
     * @return a String representing the QR code URL
     */
    public String getQrCodeURL() {
        return qrCodeURL;
    }

    /**
     * Sets the date and time when the event takes place.
     *
     * @param dateTime
     *        a Timestamp representing the event's scheduled date and time
     */
    public void setDateTime(Timestamp dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Returns the date and time when the event takes place.
     *
     * @return a Timestamp representing the event's scheduled date and time
     */
    public Timestamp getDateTime() {
        return dateTime;
    }

    /**
     * Returns the date and time when this event was created.
     *
     * @return a Timestamp representing the event creation date
     */
    public Timestamp getDateCreated() {
        return dateCreated;
    }

    /**
     * Callback interface for asynchronously retrieving the event organizer.
     * Used with the {@link #lookupOrganizer(OrganizerCallback)} method to handle
     * the asynchronous Firebase query results.
     */
    public interface OrganizerCallback {
        /**
         * Called when the organizer is successfully found.
         *
         * @param organizer
         *        the Profile of the organizer, or null if not found
         */
        void onOrganizerFound(Profile organizer);

        /**
         * Called when an error occurs during the lookup.
         *
         * @param e
         *        the Exception that occurred
         */
        void onError(Exception e);
    }

    /**
     * Looks up the organizer for the event from the organizer ID.
     * Asynchronously retrieves the organizer's Profile from Firebase Firestore
     * and returns it via the provided callback.
     *
     * @param callback
     *        the OrganizerCallback to handle the result or error
     */
    public void lookupOrganizer(OrganizerCallback callback) {
        if (this.getOrgId() == null) {
            Log.d("lookupOrganizer", "Org ID is null");
            callback.onOrganizerFound(null);
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Profiles").document(this.getOrgId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Profile organizer = documentSnapshot.toObject(Profile.class);
                        // Send the data back to the caller
                        callback.onOrganizerFound(organizer);
                    } else {
                        Log.d("lookupOrganizer", "Organizer not found in DB");
                        callback.onOrganizerFound(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("lookupOrganizer", "Firebase error", e);
                    callback.onOrganizerFound(null);
                });
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
        qrCodeURL = in.readString();
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
        dest.writeString(qrCodeURL);
        if (Radius == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(Radius);
        }
    }

}
