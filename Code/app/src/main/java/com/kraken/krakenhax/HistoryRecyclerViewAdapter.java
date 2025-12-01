package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;


/**
 * Adapter to display a list of history {@link Action} items in a RecyclerView.
 *
 * <p>Each row shows a timestamp and a short description. Descriptions that
 * reference events or users are resolved via the provided ViewModel instances
 * (may be null in edge cases; a fallback string will be used then).</p>
 */
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MyViewHolder> {
    private final List<Action> data;
    private final EventViewModel eventViewModel;
    private final ProfileViewModel profileViewModel;
    private ItemClickListener clickListener;

    /**
     * Create a new adapter.
     *
     * @param data             the list of Action items to display (must not be null)
     * @param eventViewModel   ViewModel used to resolve event titles (may be null)
     * @param profileViewModel ViewModel used to resolve profile/user names (may be null)
     */
    public HistoryRecyclerViewAdapter(List<Action> data, EventViewModel eventViewModel, ProfileViewModel profileViewModel) {
        this.data = data;
        this.eventViewModel = eventViewModel;
        this.profileViewModel = profileViewModel;
    }

    /**
     * Inflate and return a new ViewHolder for a row.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }


    /**
     * Bind data for the item at the given position. This fills in the
     * timestamp and a resolved description. Event/profile lookups are
     * performed asynchronously via the provided ViewModels and will update
     * the row when the lookup completes.
     *
     * @param holder   the ViewHolder to bind
     * @param position adapter position of the item
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Action action = data.get(position);

        // Set the timestamp
        Timestamp timestamp = action.getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy, hh:mm a", Locale.getDefault());
        String formattedTimestamp = formatter.format(timestamp.toDate());
        holder.tvActionTitle.setText(formattedTimestamp);

        // Set the action description

        // Possible action names:

        // Removed user from wait list
        // Removed user from won list
        // Removed user from lost list
        // Removed user from accept list
        // Removed user from cancel list

        // Removed from wait list
        // Removed from won list
        // Removed from lost list
        // Removed from accept list
        // Removed from cancel list

        // Join waitlist
        // Accept place in event
        // Decline place in event
        // Withdraw from waitlist

        // Triggered lottery for event
        // Lose lottery for event

        // Use eventViewModel and profileViewModel provided by the Activity (may be null in some edge cases)

        // Helper fallback text when we can't resolve names
        Supplier<String> fallback = () -> String.format("%s Related event ID: %s Related profile ID: %s", action.getAction(), action.getAssociatedEventID(), action.getAssociatedUserID());

        switch (action.getAction()) {
            // From entrants perspective
            case "Join waitlist":
                setActionDescriptionEvent(
                        "Joined waitlist for event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Withdraw from waitlist":
                setActionDescriptionEvent(
                        "Withdrew from waitlist for event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Accept place in event":
                setActionDescriptionEvent(
                        "Accepted place in event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Decline place in event":
                setActionDescriptionEvent(
                        "Declined place in event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Removed from wait list":
                setActionDescriptionEventAndUser(
                        "Removed from waitlist for event: %s by organizer: %s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed from won list":
                setActionDescriptionEventAndUser(
                        "Removed from won list for event: %s by organizer: %s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed from lost list":
                setActionDescriptionEventAndUser(
                        "Removed from lost list for event: %s by organizer: %s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed from accept list":
                setActionDescriptionEventAndUser(
                        "Removed from accept list for event: %s by organizer: %s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed from cancel list":
                setActionDescriptionEventAndUser(
                        "Removed from cancel list for event: %s by organizer: %s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Lose lottery for event":
                setActionDescriptionEvent(
                        "Lost lottery for event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Won lottery for event":
                setActionDescriptionEvent(
                        "Won lottery for event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Selected as replacement winner for event":
                setActionDescriptionEvent(
                        "Selected as replacement winner for event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            // From organizers perspective
            case "Triggered lottery for event":
                setActionDescriptionEvent(
                        "Triggered lottery for event: %s",
                        action.getAssociatedEventID(), holder, fallback);
                break;

            case "Removed user from wait list":
                setActionDescriptionEventAndUser(
                        "Removed user: %2$s from waitlist for event: %1$s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed user from won list":
                setActionDescriptionEventAndUser(
                        "Removed user: %2$s from won list for event: %1$s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed user from lost list":
                setActionDescriptionEventAndUser(
                        "Removed user: %2$s from lost list for event: %1$s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed user from accept list":
                setActionDescriptionEventAndUser(
                        "Removed user: %2$s from accept list for event: %1$s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            case "Removed user from cancel list":
                setActionDescriptionEventAndUser(
                        "Removed user: %2$s from cancel list for event: %1$s",
                        action.getAssociatedEventID(), action.getAssociatedUserID(), holder, fallback);
                break;

            default:
                holder.tvActionDescription.setText(fallback.get());
                break;
        }

        // Note: Each case updates holder.tvActionDescription inside callbacks where necessary.
    }

    /**
     * Sets the action description for cases that require only an event.
     *
     * @param formatString format string that expects the event title
     * @param eventID      id of the event to resolve
     * @param holder       view holder to update
     * @param fallback     supplier for fallback text
     */
    private void setActionDescriptionEvent(String formatString, String eventID, MyViewHolder holder, java.util.function.Supplier<String> fallback) {
        // 1. Early exit if view models are missing
        if (eventViewModel == null) {
            holder.tvActionDescription.setText(fallback.get());
            return;
        }

        // 2. Lookup event
        eventViewModel.lookupEvent(eventID, event -> {
            if (event == null) {
                holder.tvActionDescription.setText(fallback.get());
                return;
            }

            String text = String.format(formatString, event.getTitle());
            holder.tvActionDescription.setText(text);
        });
    }

    /**
     * Sets the action description for cases that require an event and a profile.
     *
     * @param formatString format string that expects the event title and organizer username
     * @param eventId      id of the event to resolve
     * @param userId       id of the user/profile to resolve
     * @param holder       view holder to update
     * @param fallback     supplier for fallback text
     */
    private void setActionDescriptionEventAndUser(String formatString, String eventId, String userId,
                                                  MyViewHolder holder, java.util.function.Supplier<String> fallback) {
        // 1. Early exit if ViewModels are missing
        if (eventViewModel == null || profileViewModel == null) {
            holder.tvActionDescription.setText(fallback.get());
            return;
        }

        // 2. Look up Event
        eventViewModel.lookupEvent(eventId, event -> {
            if (event == null) {
                holder.tvActionDescription.setText(fallback.get());
                return;
            }

            // 3. Look up Profile (Organizer) inside Event callback
            profileViewModel.lookupProfile(userId, organizer -> {
                if (organizer != null) {
                    // Success: Format and set text
                    String text = String.format(formatString, event.getTitle(), organizer.getUsername());
                    holder.tvActionDescription.setText(text);
                } else {
                    holder.tvActionDescription.setText(fallback.get());
                }
            });
        });
    }

    /**
     * Return the number of items in the adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Replace the adapter's data and notify the view.
     *
     * @param newData new list of Action items
     */
    public void updateData(List<Action> newData) {
        this.data.clear();
        this.data.addAll(newData);
        notifyDataSetChanged();
    }

    /**
     * Set a click listener for row items.
     *
     * @param itemClickListener listener to notify when a row is clicked
     */
    @SuppressWarnings("unused")
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * Return the Action at the given adapter position.
     *
     * @param id adapter position
     * @return Action at position
     */
    @SuppressWarnings("unused")
    public Action getItem(int id) {
        return data.get(id);
    }

    /**
     * Listener interface for item click events.
     */
    public interface ItemClickListener {
        /**
         * Called when an item is clicked.
         *
         * @param view     clicked view
         * @param position adapter position of clicked item
         */
        void onItemClick(View view, int position);
    }

    /**
     * ViewHolder for history rows. Exposes the title and description TextViews
     * and wires the click events to the adapter's {@link ItemClickListener}.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvActionTitle;
        final TextView tvActionDescription;

        /**
         * Create a ViewHolder and bind view references.
         *
         * @param itemView root view for the row
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvActionTitle = itemView.findViewById(R.id.tv_action_title);
            tvActionDescription = itemView.findViewById(R.id.tv_action_description);

            itemView.setOnClickListener(this);
        }

        /**
         * Forward click events to the registered ItemClickListener (if any).
         */
        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(view, position);
                }
            }
        }
    }

}
