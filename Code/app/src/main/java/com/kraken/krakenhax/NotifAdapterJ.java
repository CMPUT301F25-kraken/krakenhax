package com.kraken.krakenhax;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotifAdapterJ extends ArrayAdapter<NotificationJ> {
    //public Profile organizer;
    //public Profile Recipient;
    public ArrayList<NotificationJ> notifList;
    public TextView title;
    public TextView recipient;
    public TextView dateV;
    public TextView message;

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
        title = view.findViewById(R.id.TitleDisplay);
        recipient = view.findViewById(R.id.RecipientDisplay);
        dateV = view.findViewById(R.id.DateView);
        message = view.findViewById(R.id.MessageDisplay);

        NotificationJ notification = getItem(position);

        assert notification != null;
        //Log.d("display Notification", notification.getBody());
        String recipientUsername = notification.getRecipient();
        recipientUsername = "Recipient"+ recipientUsername;
        Log.d("show senderID", "RecipientID:"+recipientUsername);
        Timestamp ts = notification.getTimestamp();
        // format timestamp
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
