package com.kraken.krakenhax;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * ArrayAdapter to display a list of all notifications to the admin.
 */
public class NotifAdapterAdmin extends ArrayAdapter<NotificationJ> {
    public ArrayList<NotificationJ> notifList;
    public TextView title;
    public TextView recipient;
    public TextView dateV;
    public TextView message;

    /**
     * Constructor for the NotificationAdapter
     * @param context Gets the context for where the list is being generated.
     * @param notifList Requires an arrayList of notifications to pull from.
     */
    public NotifAdapterAdmin(Context context, ArrayList<NotificationJ> notifList) {
        super(context,0, notifList);
        this.notifList = notifList;

    }

    /**
     * Takes an item from the notification arraylist and displays the information in the specified format to be displayed in the listView.
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return Returns the display format of a notification class to add to the viewList.
     */
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_format, parent, false);
        }
        title = view.findViewById(R.id.TitleDisplay);
        recipient = view.findViewById(R.id.RecipientDisplay);
        dateV = view.findViewById(R.id.DateView);
        message = view.findViewById(R.id.MessageDisplay);

        NotificationJ notification = getItem(position);
        assert notification != null;

        String recipientUsername = notification.getRecipient();
        recipientUsername = "Recipient: "+ recipientUsername;
        Log.d("show senderID", "RecipientID:"+recipientUsername);
        Timestamp ts = notification.getTimestamp();
        if (notification.getTimestamp() != null) {
            Date date = ts.toDate();
            SimpleDateFormat sdf =
                    new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
            dateV.setText(sdf.format(date));
        } else {
            dateV.setText("");
        }

        recipient.setText(recipientUsername);
        message.setText(notification.getBody());
        title.setText(notification.getTitle());

        return view;
    }

}
