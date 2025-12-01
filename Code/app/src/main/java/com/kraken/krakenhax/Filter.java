package com.kraken.krakenhax;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Provides filtering logic for events based on a user's preferences
 * such as categories and availability.
 */
public class Filter {
    private Profile user; //Currently signed in user
    private ArrayList<Event> events; //All events present in Firebase
    private ArrayList<Event> filteredEvents; //Events that match the filter
    private ArrayList<Timestamp> availability; //A list of available days for the user
    private ArrayList<String> categories; //A list of categories the user is interested in

    /**
     * Creates a new {@code Filter} for the given user and list of events.
     *
     * @param user   the profile of the currently signed-in user
     * @param events the list of all events to be filtered
     */
    public Filter(Profile user, ArrayList<Event> events) {
        this.user = user;
        this.events = events;
        this.filteredEvents = new ArrayList<Event>();
        this.availability = new ArrayList<Timestamp>();
        this.categories = new ArrayList<String>();

    }

    /**
     * Returns the profile of the currently signed-in user.
     *
     * @return the current user profile
     */
    public Profile getUser() {
        return user;
    }

    /**
     * Sets the profile of the currently signed-in user.
     *
     * @param user the user profile to set
     */
    public void setUser(Profile user) {
        this.user = user;
    }

    /**
     * Returns the list of all events available for filtering.
     *
     * @return the list of events
     */
    public ArrayList<Event> getEvents() {
        return events;
    }

    /**
     * Replaces the current list of events with the given list.
     *
     * @param events the new list of events
     */
    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    /**
     * Adds a single event to the list of events.
     *
     * @param event the event to add
     */
    public void addEvent(Event event) {
        this.events.add(event);
    }

    /**
     * Returns the list of events that matched the last applied filter.
     *
     * @return the filtered events
     */
    public ArrayList<Event> getFilteredEvents() {
        return filteredEvents;
    }

    /**
     * Applies the current filter criteria (categories and availability)
     * to the events list and updates the filtered events in order of
     * best match.
     */
    public void setFilter() {
        HashMap<Event, Integer> matchScore = new HashMap<Event, Integer>();
        for (Event event : events) {
            int score = 0;
            for (String userCategory : this.categories) {
                if (event.getCategories().contains(userCategory)) {
                    score++;
                }
            }
            /**
             boolean dateMatchFound = false;
             for (Timestamp userAvailability : this.availability) {
             for (Timestamp eventDay : event.getTimeframe()) {
             if (isSameDay(userAvailability, eventDay)) {
             score++;
             dateMatchFound = true;
             }
             }
             }
             **/
            if (score > 0) {
                matchScore.put(event, score);
            }
        }
        // Create a list from the elements of the HashMap
        List<Map.Entry<Event, Integer>> sortedEntries = new ArrayList<>(matchScore.entrySet());
        // Sort the list in descending order of scores. [18]
        Collections.sort(sortedEntries, new Comparator<Map.Entry<Event, Integer>>() {
            @Override
            public int compare(Map.Entry<Event, Integer> e1, Map.Entry<Event, Integer> e2) {
                // For descending order
                return e2.getValue().compareTo(e1.getValue());
            }
        });
        // Clear the existing filteredEvents list. [3, 4, 5, 6, 7]
        this.filteredEvents.clear();

        // Add the sorted events to the filteredEvents list
        for (Map.Entry<Event, Integer> entry : sortedEntries) {
            this.filteredEvents.add(entry.getKey());
        }
    }

    /**
     * Determines whether two timestamps fall on the same calendar day
     * in the default time zone.
     *
     * @param timestamp1 the first timestamp to compare
     * @param timestamp2 the second timestamp to compare
     * @return {@code true} if both timestamps represent the same day; {@code false} otherwise
     */
    public boolean isSameDay(Timestamp timestamp1, Timestamp timestamp2) {
        if (timestamp1 == null || timestamp2 == null) {
            return false;
        }
        Calendar calendar1 = Calendar.getInstance(TimeZone.getDefault());
        Calendar calendar2 = Calendar.getInstance(TimeZone.getDefault());
        calendar1.setTime(timestamp1.toDate());
        calendar2.setTime(timestamp2.toDate());
        return calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Returns the list of days the user is available.
     *
     * @return the user's availability as a list of timestamps
     */
    public ArrayList<Timestamp> getAvailability() {
        return availability;
    }

    /**
     * Sets the list of days the user is available.
     *
     * @param availability the list of available timestamps
     */
    public void setAvailability(ArrayList<Timestamp> availability) {
        this.availability = availability;
    }

    /**
     * Adds a single available day to the user's availability list.
     *
     * @param availability the available timestamp to add
     */
    public void addAvailability(Timestamp availability) {
        this.availability.add(availability);
    }

    /**
     * Returns the list of categories the user is interested in.
     *
     * @return the list of user categories
     */
    public ArrayList<String> getCategories() {
        return categories;
    }

    /**
     * Sets the list of categories the user is interested in.
     *
     * @param categories the list of categories to set
     */
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    /**
     * Adds a single category to the user's list of interests.
     *
     * @param category the category to add
     */
    public void addCategory(String category) {
        this.categories.add(category);
    }

}
