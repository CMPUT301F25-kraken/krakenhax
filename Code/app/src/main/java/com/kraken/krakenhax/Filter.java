package com.kraken.krakenhax;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
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
            int score = 0;
            for (String userCategory : this.categories) {
                if (event.getCategories().contains(userCategory)) {
                    score++;
                }
            }
            boolean dateMatchFound = false;
            for (Timestamp userAvailability : this.availability) {
                for (Timestamp eventDay : event.getTimeframe()) {
                    if (isSameDay(userAvailability, eventDay)) {
                        score++;
                        dateMatchFound = true;
                    }
                }
            }
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

}
