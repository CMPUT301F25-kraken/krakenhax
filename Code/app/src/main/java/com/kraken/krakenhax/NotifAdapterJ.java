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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotifAdapterJ extends ArrayAdapter<NotificationJ> {
    public Profile organizer;
    public Profile Recipient;
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
        String senderProfileID = notification.getSender();
        String recipientProfileID = notification.getRecipient();

        if (senderProfileID != null && !senderProfileID.isEmpty()) {
            DocumentReference senderRef = FirebaseFirestore.getInstance().collection("Profiles").document(senderProfileID);
            senderRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Profile senderProfile = documentSnapshot.toObject(Profile.class);
                    if (senderProfile != null) {
                        sender.setText(senderProfile.getUsername());
                    }
                }
            });
        }

        if (recipientProfileID != null && !recipientProfileID.isEmpty()) {
            DocumentReference recipientRef = FirebaseFirestore.getInstance().collection("Profiles").document(recipientProfileID);
            recipientRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Profile recipientProfile = documentSnapshot.toObject(Profile.class);
                    if (recipientProfile != null) {
                        recipient.setText(recipientProfile.getUsername());
                    }
                }
            });
        }
        message.setText(notification.getBody());
        title.setText(notification.getTitle());



        return view;
    }


}
