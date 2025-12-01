package com.kraken.krakenhax;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Filter {
    private Profile user; //Currently signed in user
    private ArrayList<Event> events; //All events present in Firebase
    private ArrayList<Event> filteredEvents; //Events that match the filter
    private ArrayList<Timestamp> availability; //A list of available days for the user
    private ArrayList<String> categories; //A list of categories the user is interested in


    public Filter(Profile user, ArrayList<Event> events) {
        this.user = user;
        this.events = events;
        this.filteredEvents = new ArrayList<Event>();
        this.availability = new ArrayList<Timestamp>();
        this.categories = new ArrayList<String>();

    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public ArrayList<Event> getFilteredEvents() {
        return filteredEvents;
    }

    public void setFilter() {
        HashMap<Event, Integer> matchScore = new HashMap<Event, Integer>();
        for (Event event : events) {
            boolean isAvailable = false;
            if (availability == null || availability.isEmpty()) {
                isAvailable = true;
            }
            // If a date filter is applied, check if the event is within the range.
            else if (event.getDateTime() != null) {
                // Ensure we have a valid range with two dates.
                if (availability.size() >= 2) {
                    Timestamp eventDate = event.getDateTime();
                    Timestamp startDate = availability.get(0);
                    Timestamp endDate = availability.get(1);

                    // Check if the event's date is within the selected range (inclusive).
                    // The logic is: eventDate must NOT be before startDate AND must NOT be after endDate.
                    if (!eventDate.toDate().before(startDate.toDate()) && !eventDate.toDate().after(endDate.toDate())) {
                        isAvailable = true;
                    }
                }
            }

            if (!isAvailable) {
                continue;
            }

            int score = 0;
            if (this.categories != null && !this.categories.isEmpty()) {
                for (String userCategory : this.categories) {
                    if (event.getCategories().contains(userCategory)) {
                        score++;
                    }
                }
                if (score > 0) {
                    matchScore.put(event, score);
                }
            } else {
                if (isAvailable) {
                    matchScore.put(event, 0);
                }
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

    public ArrayList<Timestamp> getAvailability() {
        return availability;
    }

    public void setAvailability(ArrayList<Timestamp> availability) {
        this.availability = availability;
    }
    public void addAvailability(Timestamp availability) {
        this.availability.add(availability);
    }

    public ArrayList<String> getCategories() {
        return categories;
    }
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
    public void addCategory(String category) {
        this.categories.add(category);
    }

}
