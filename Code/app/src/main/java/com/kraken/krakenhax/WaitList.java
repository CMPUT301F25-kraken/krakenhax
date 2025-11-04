package com.kraken.krakenhax;

import java.util.ArrayList;

/**
 * Represents a waitlist for an event.
 * Tracks entrants who have registered but not yet been selected.
 * Organizers can view and promote entrants from the waitlist to the WonList.
 *
 * <p>Collaborators: Event, Entrant, WonList, LostList</p>
 *
 * @author Amaan
 * @version 1.1
 */
public class WaitList {
    private final ArrayList<Profile> entrants = new ArrayList<>();

    /**
     * Adds a new entrant to the waitlist.
     * @param entrant the entrant to be added
     * @throws IllegalArgumentException if entrant is null
     */
    public void addEntrant(Profile entrant) {
        if (entrant == null) {
            throw new IllegalArgumentException("Entrant cannot be null.");
        }
        if (!entrants.contains(entrant)) {
            entrants.add(entrant);
        }
    }

    /**
     * Removes an entrant from the waitlist.
     * @param entrant the entrant to remove
     */
    public void removeEntrant(Profile entrant) {
        entrants.remove(entrant);
    }

    /**
     * Returns a list of all entrants currently on the waitlist.
     * @return list of entrant profiles
     */
    public ArrayList<Profile> getEntrants() {
        return entrants;
    }

    /**
     * Checks if the waitlist is empty.
     * @return true if no entrants remain
     */
    public boolean isEmpty() {
        return entrants.isEmpty();
    }

    /**
     * Clears all entrants (used if event resets).
     */
    public void clearEntrants() {
        entrants.clear();
    }

    /**
     * Sends a notification to all entrants on the waitlist.
     * @param notifier notification handler
     * @param message text to send
     */
    public void notifyEntrants(NotifyUser notifier, String message) {
        if (notifier == null) return;
        for (Profile entrant : entrants) {
            notifier.sendNotification(entrant, message);
        }
    }
}
