package com.kraken.krakenhax;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * This class represents the data for an event.
 */
public class Event implements Parcelable {
    private String id;
    private String title; //Done
    private ArrayList<String> categories; //Done
    private ArrayList<ZonedDateTime> timeframe; //Done
    private String eventDetails; //Done
    private String location; //Done
    private Integer Radius; //Done
    private ArrayList<Profile> cancelList;
    private ArrayList<Profile> waitList;
    private ArrayList<Profile> lostList;
    private ArrayList<Profile> wonList;
    private String poster;
    //private Bitmap poster; //*

    /**
     * Constructor for Event class.
     */
    public Event() {
        this.id = "";
        this.title = "";
        this.categories = new ArrayList<String>();
        this.timeframe = new ArrayList<ZonedDateTime>();
        this.eventDetails = "";
        this.location = "";
        this.Radius = 0;
        this.poster = null;
        this.cancelList = new ArrayList<Profile>();
        this.waitList = new ArrayList<Profile>();
        this.lostList = new ArrayList<Profile>();
        this.wonList = new ArrayList<Profile>();
    }


    /**
     * Returns the id of the event.
     * @return
     *        a String representing the event id
     */
    public String getId() {return id;}

    /**
     * Sets the id of the event.
     * @param id
     *        a String representing the event id
     */
    public void setId(String id) {
        this.id = id;
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
        //  this.cancelList = new CancelList();
        //this.waitList = new WaitList();
        //this.lostList = new LostList();
        //this.wonList = new WonList();
    }

    /**
     * Returns the title of the event.
     * @return
     *        a String representing the event title
     */
    public String getTitle() {return title;}

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
        if (location.matches("\\d+")) {
            this.location = location;
        } else {
            throw new IllegalArgumentException("Location must contain a numerical address");
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
     * Returns the list of profiles who won the lottery but declined their spot.
     * @return
     *        an ArrayList of Profiles who cancelled their spot
     */
    public ArrayList<Profile> getCancelList() {
        return waitList;
    }

    /**
     * Returns the list of profiles who lost the lottery.
     * @return
     *        an ArrayList of Profiles who lost the lottery
     */
    public ArrayList<Profile> getLostList() {
        return waitList;
    }

    /**
     * Returns the list of profiles who won the lottery.
     * @return
     *        an ArrayList of Profiles who won the lottery
     */
    public ArrayList<Profile> getWonList() {
        return waitList;
    }

    /**
     * Returns the list of profiles who signed up for the lottery.
     * @return
     *        an ArrayList of Profiles who signed up for the lottery.
     */
    public ArrayList<Profile> getWaitList() {
        return waitList;
    }

    /**
     * Adds a new profile to the waitlist for this event.
     * @param profile
     *        Profile of the entrant to be added to the waitlist
     * @throws IllegalArgumentException
     *        if the profile is already in the waitlist
     */
    public void addToWaitList(Profile profile) {
        if (this.waitList.contains(profile)) {
            throw new IllegalArgumentException("Profile is already in the waitlist");
        } else {
            this.waitList.add(profile);
        }
    }

    /**
     * Removes a profile from the waitlist for this event.
     * @param profile
     *        Profile of the entrant to be removed to the waitlist
     * @throws IllegalArgumentException
     *        if the profile isn't on the waitlist
     */
    public void removeFromWaitList(Profile profile) {
        if (this.waitList.contains(profile)) {
            this.waitList.remove(profile);

        } else {
            throw new IllegalArgumentException("Profile is not on the waitlist");
        }
    }

    /**
     * Declines a won spot in the event, adding the profile to the cancel list.
     * @param profile
     *        Profile of the entrant who is cancelling their won spot
     * @throws IllegalArgumentException
     *        if the profile hasnt won a spot to cancel
     *        if the profile has already cancelled
     */
    public void addToCancelList(Profile profile) {
        if (this.cancelList.contains(profile)) {
            throw new IllegalArgumentException("Profile has already cancelled");
        }

        if (this.wonList.contains(profile)) {
            this.cancelList.add(profile);
            this.wonList.remove(profile);
        } else {
            throw new IllegalArgumentException("Profile has not won a spot to cancel");
        }

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

        for (int i = 0; i < numberOfWinners && i < shuffled.size(); i++) {
            Profile winner = shuffled.get(i);
            winners.add(winner);
            losers.remove(winner);
        }

        this.lostList = losers;
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

    }

}
