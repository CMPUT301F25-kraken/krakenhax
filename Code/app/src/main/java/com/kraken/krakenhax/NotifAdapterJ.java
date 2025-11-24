package com.kraken.krakenhax;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotifAdapterJ extends ArrayAdapter<NotificationJ> {

    public ArrayList<NotificationJ> notifList;

    public NotifAdapterJ(Context context, ArrayList<NotificationJ> notifList) {
        super(context,0, notifList);
        this.notifList = notifList;

    }


    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_format, parent, false);
        }
        TextView title = view.findViewById(R.id.TitleDisplay);
        TextView recipient = view.findViewById(R.id.RecipientDisplay);
        TextView sender = view.findViewById(R.id.UsernameDisplay);
        TextView message = view.findViewById(R.id.MessageDisplay);

        NotificationJ notification = getItem(position);
        assert notification != null;
        sender.setText(notification.getSender());
        message.setText(notification.getBody());
        title.setText(notification.getTitle());
        recipient.setText(notification.getRecipient());

        return view;
    }


}
