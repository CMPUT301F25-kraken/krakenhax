package com.kraken.krakenhax;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import android.graphics.Bitmap;

/**
 * This class represents the data for an event.
 */
public class Event {
    private String title; //Done
    private ArrayList<String> categories; //Done
    private ArrayList<ZonedDateTime> timeframe; //Done
    private String eventDetails; //Done
    private String location; //Done
    private Integer Radius; //Done
    private Bitmap poster; //*
    //private CancelList cancelList;
    //private WaitList waitList;
    //private LostList lostList;
    //private WonList wonList;

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

    /*
    TODO: Implement WaitList, CanceList, WonList, LostList classes and add to Event class.
     */
}
