package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Dialog fragment to display a notification.
 * Allows the admin to view a notification.
 */
public class ViewNotification extends DialogFragment {
    public NotificationJ notif;


    /**
     * Required empty public constructor
     */
    public ViewNotification() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of the dialog fragment.
     * Bundles the notification to be displayed.
     * @param notification Takes a notification to display.
     *
     * @return Returns the new instance of the dialog fragment.
     */
    public static ViewNotification newInstance(NotificationJ notification){
        Bundle args = new Bundle();
        args.putSerializable("Notification", notification);
        ViewNotification fragment = new ViewNotification();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * Contains the main functionality of the fragment.
     * Sets up the dialog fragment.
     * Formats the notification to be displayed.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Returns the View for the fragment's UI, or null.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_notif, null);
        TextView Title= view.findViewById(R.id.NotTitle);
        TextView dateV = view.findViewById(R.id.NotDate);
        TextView Recip = view.findViewById(R.id.NotRecip);
        TextView Sender = view.findViewById(R.id.NotSend);
        TextView Body = view.findViewById(R.id.NotBody);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (getArguments() != null) {
            notif = (NotificationJ) getArguments().getSerializable("Notification");
        }
        if (notif != null) {
            Title.setText(notif.getTitle());
            Timestamp ts = notif.getTimestamp();
            // format timestamp
            if (notif.getTimestamp() != null) {

                Date date = ts.toDate();
                SimpleDateFormat sdf =
                        new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());
                dateV.setText(sdf.format(date));
            }
            String recipient= notif.getRecipient();
            recipient = "Recipient: "+recipient;
            Recip.setText(recipient);
            String sender = notif.getSender();
            sender = "Sender: "+ sender;
            Sender.setText(sender);
            String body = notif.getBody();
            body = "Message: "+ body;
            Body.setText(body);
        }
        return builder
                .setView(view)
                .setTitle("Notification")
                .setNegativeButton("close", (dialog, which)->{
                    dialog.dismiss();
                })
                .create();


    }

}
