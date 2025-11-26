package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifAdapterS extends RecyclerView.Adapter<NotifAdapterS.NotifViewHolder> {

    public interface OnNotifClickListener {
        void onNotifClick(NotificationJ notif);
    }

    private List<NotificationJ> notifications = new ArrayList<>();
    private final OnNotifClickListener listener;

    public NotifAdapterS(OnNotifClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<NotificationJ> list) {
        this.notifications = list;
        notifyDataSetChanged();
    }


    @Override
    public NotifViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NotifViewHolder holder, int position) {
        NotificationJ notif = notifications.get(position);
        holder.bind(notif, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvBody, tvTime;


        public NotifViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.notifTitle);
            tvBody = itemView.findViewById(R.id.notifBody);
            tvTime = itemView.findViewById(R.id.notifTime);

        }

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



