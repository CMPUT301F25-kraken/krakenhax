package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter that displays a list of {@link NotificationJ} items.
 * <p>
 * Binds title, body and a formatted timestamp to each item view and forwards
 * item clicks to an {@link OnNotifClickListener}.
 * <p>
 * Fields:
 * - {@code listener}: listener for item clicks.
 * - {@code notifications}: current list of {@link NotificationJ} displayed by the adapter.
 */
public class NotifAdapterS extends RecyclerView.Adapter<NotifAdapterS.NotifViewHolder> {
    private final OnNotifClickListener listener;
    private List<NotificationJ> notifications = new ArrayList<>();

    /**
     * Create the adapter with a click listener.
     *
     * @param listener listener for item click events; may be null
     */
    public NotifAdapterS(OnNotifClickListener listener) {
        this.listener = listener;
    }

    /**
     * Replace the adapter's notification list and refresh the view.
     *
     * @param list new list of {@link NotificationJ} to display; may be null
     */
    public void setNotifications(List<NotificationJ> list) {
        this.notifications = list;
        notifyDataSetChanged();
    }

    /**
     * Inflate the item view and create a new ViewHolder.
     *
     * @param parent   parent ViewGroup
     * @param viewType view type integer
     * @return a new NotifViewHolder instance
     */
    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(v);
    }

    /**
     * Bind data to the ViewHolder at the given position.
     *
     * @param holder   view holder to bind
     * @param position position of the item in the list
     */
    @Override
    public void onBindViewHolder(NotifViewHolder holder, int position) {
        NotificationJ notif = notifications.get(position);
        holder.bind(notif, listener);
    }

    /**
     * Return the current number of notifications.
     *
     * @return size of the notifications list
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * Callback interface for notification item clicks.
     */
    public interface OnNotifClickListener {
        /**
         * Called when a notification item is clicked.
         *
         * @param notif the clicked {@link NotificationJ}
         */
        void onNotifClick(NotificationJ notif);
    }

    /**
     * ViewHolder that binds notification data to the item view.
     */
    public static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvTime;

        /**
         * Create a new ViewHolder for the provided item view.
         *
         * @param itemView view for a single notification item
         */
        public NotifViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.notifTitle);
            tvBody = itemView.findViewById(R.id.notifBody);
            tvTime = itemView.findViewById(R.id.notifTime);
        }

        /**
         * Bind a {@link NotificationJ} to the view and set up click handling.
         *
         * @param notif    notification data to display
         * @param listener click listener to notify when the item is clicked; may be null
         */
        void bind(NotificationJ notif, OnNotifClickListener listener) {
            tvTitle.setText(notif.getTitle());
            tvBody.setText(notif.getBody());
            Timestamp ts = notif.getTimestamp();
            // format timestamp
            if (notif.getTimestamp() != null) {

                Date date = ts.toDate();
                SimpleDateFormat sdf =
                        new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
                tvTime.setText(sdf.format(date));

            } else {
                tvTime.setText("");
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onNotifClick(notif);
            });
        }
    }
}
