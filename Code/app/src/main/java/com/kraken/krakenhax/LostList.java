package com.kraken.krakenhax;

import java.util.ArrayList;

/**
 * Tracks entrants who were not selected after the lottery draw.
 * Entrants here can later be promoted to WonList if spots open.
 *
 * <p>Collaborators: WonList, WaitList, NotifyUser</p>
 *
 * @author Amaan
 * @version 1.1
 */
public class LostList {
    private final ArrayList<Profile> losers = new ArrayList<>();

    /**
     * Adds an entrant to the lost list if not already present.
     * @param entrant entrant to be marked as lost
     * @throws IllegalArgumentException if entrant is null
     */
    public void addLoser(Profile entrant) {
        if (entrant == null) {
            throw new IllegalArgumentException("Entrant cannot be null.");
        }
        if (!losers.contains(entrant)) {
            losers.add(entrant);
        }
    }

    /**
     * Removes an entrant from the lost list (if later promoted or withdrawn).
     * @param entrant entrant to remove
     */
    public void removeLoser(Profile entrant) {
        losers.remove(entrant);
    }

    /**
     * Returns a list of all entrants currently in the lost list.
     * @return list of lost entrants
     */
    public ArrayList<Profile> getLosers() {
        return losers;
    }

    /**
     * Clears all entrants from the lost list.
     */
    public void clearLosers() {
        losers.clear();
    }

    /**
     * Sends a notification to all entrants in the lost list.
     * @param notifier notification handler
     */
    public void notifyLosers(NotifyUser notifier) {
        if (notifier == null) return;
        for (Profile entrant : losers) {
            notifier.sendNotification(entrant, "Unfortunately, you were not selected for this event.");
        }
    }
}
