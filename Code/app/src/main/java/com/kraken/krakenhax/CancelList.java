package com.kraken.krakenhax;

import java.util.ArrayList;

/**
 * Tracks entrants who have cancelled their participation in an event.
 * When an entrant cancels, they are removed from other lists and recorded here.
 *
 * <p>Collaborators: WaitList, WonList, LostList, NotifyUser</p>
 *
 * @author Amaan
 * @version 1.1
 */
public class CancelList {
    private final ArrayList<Profile> cancelled = new ArrayList<>();

    /**
     * Adds an entrant to the cancellation list.
     * @param entrant entrant profile that cancelled
     * @throws IllegalArgumentException if entrant is null
     */
    public void addCancelled(Profile entrant) {
        if (entrant == null) {
            throw new IllegalArgumentException("Entrant cannot be null.");
        }
        if (!cancelled.contains(entrant)) {
            cancelled.add(entrant);
        }
    }

    /**
     * Removes an entrant from the cancellation list.
     * @param entrant entrant to remove
     */
    public void removeCancelled(Profile entrant) {
        cancelled.remove(entrant);
    }

    /**
     * Clears the cancellation list (used when resetting an event).
     */
    public void clearCancelled() {
        cancelled.clear();
    }

    /**
     * Returns a list of all entrants who have cancelled.
     * @return list of cancelled profiles
     */
    public ArrayList<Profile> getCancelled() {
        return cancelled;
    }

    /**
     * Notifies all cancelled entrants of successful cancellation.
     * @param notifier notification handler
     */
    public void notifyCancelled(NotifyUser notifier) {
        if (notifier == null) return;
        for (Profile entrant : cancelled) {
            notifier.sendNotification(entrant, "Your registration has been cancelled successfully.");
        }
    }
}
