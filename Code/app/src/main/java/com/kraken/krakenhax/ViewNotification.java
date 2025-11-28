package com.kraken.krakenhax;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class ViewNotification extends DialogFragment {
    public NotificationJ notif;



    public static ViewNotification newInstance(NotificationJ notification){
        Bundle args = new Bundle();
        args.putSerializable("Notification", notification);
        ViewNotification fragment = new ViewNotification();
        fragment.setArguments(args);
        return fragment;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_notif, null);
        TextView Title= view.findViewById(R.id.NotTitle);
        TextView date = view.findViewById(R.id.NotDate);
        TextView Recip = view.findViewById(R.id.NotRecip);
        TextView Sender = view.findViewById(R.id.NotSend);
        TextView Body = view.findViewById(R.id.NotBody);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (getArguments() != null) {
            notif = (NotificationJ) getArguments().getSerializable("Notification");
        }
        if (notif != null) {
            Title.setText(notif.getTitle());
            date.setText(notif.getTimestamp().toString());
            Recip.setText(notif.getRecipient());
            Sender.setText(notif.getSender());
            Body.setText(notif.getBody());
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

