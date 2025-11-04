package com.kraken.krakenhax;

import java.util.ArrayList;

/**
 * Stores entrants that have been selected from the waitlist and confirmed their spot.
 * Organizers can remove entrants before deadlines or finalize the list once sign-up closes.
 *
 * <p>Collaborators: WaitList, LostList, Event, NotifyUser</p>
 *
 * @author Amaan
 * @version 1.1
 */
public class WonList {
    private final ArrayList<Profile> winners = new ArrayList<>();

    /**
     * Adds an entrant to the won list if not already included.
     * @param entrant entrant to add
     * @throws IllegalArgumentException if entrant is null
     */
    public void addWinner(Profile entrant) {
        if (entrant == null) {
            throw new IllegalArgumentException("Entrant cannot be null.");
        }
        if (!winners.contains(entrant)) {
            winners.add(entrant);
        }
    }

    /**
     * Removes an entrant from the won list.
     * @param entrant entrant to remove
     */
    public void removeWinner(Profile entrant) {
        winners.remove(entrant);
    }

    /**
     * Returns a list of all entrants currently marked as winners.
     * @return list of entrant profiles
     */
    public ArrayList<Profile> getWinners() {
        return winners;
    }

    /**
     * Clears all winners (e.g., if event cancelled or rerun).
     */
    public void clearWinners() {
        winners.clear();
    }

    /**
     * Sends notifications to all entrants in the won list.
     * @param notifier notification handler
     */
    public void notifyWinners(NotifyUser notifier) {
        if (notifier == null) return;
        for (Profile entrant : winners) {
            notifier.sendNotification(entrant, "Congratulations! You have been selected for the event.");
        }
    }
}
