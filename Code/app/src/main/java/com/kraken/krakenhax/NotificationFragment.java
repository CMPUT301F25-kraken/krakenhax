package com.kraken.krakenhax;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    private RecyclerView rv;
    private NotifAdapterS adapter;
    private ListenerRegistration notifListener;

    private FirebaseFirestore db;
    private Profile currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        currentUser = mainActivity.currentUser;

        db = FirebaseFirestore.getInstance();

        rv = view.findViewById(R.id.recyclerNotifications);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new NotifAdapterS(notif -> {
            openEvent(notif);

        });

        rv.setAdapter(adapter);

        startNotificationListListener();

        ImageButton back = view.findViewById(R.id.backBtn);
        back.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });

        return view;
    }

    private void openEvent(NotificationJ notif) {
        String eventId = notif.getEventID();

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(requireContext(), "No event linked to this notification", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(requireContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Event event = doc.toObject(Event.class);
                    if (event == null) {
                        Toast.makeText(requireContext(), "Failed to load event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    event.setId(doc.getId());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("event", event);

                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_NotificationFragment_to_EventFragment, bundle);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error loading event", Toast.LENGTH_SHORT).show();
                    Log.e("Notifications", "Error loading event", e);
                });
    }

    private void startNotificationListListener() {
        notifListener = db.collection("Profiles")
                .document(currentUser.getID()).collection("Notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Log.e("firestore", "Listen failed", e);
                        return;
                    }
                    if (snap == null) {
                        Log.d("firestore", "Snapshot is null");
                        return;
                    }

                    Log.d("firestore", "Notifications docs: " + snap.size());
                    if (e != null || snap == null) return;
                    List<NotificationJ> list = new ArrayList<>();
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            NotificationJ n = doc.toObject(NotificationJ.class);
                            if (n == null) {
                                Log.w("Firestore", "toObject returned null for doc " + doc.getId());
                                continue;
                            }
                            Log.d("Firestore", "Loaded notif: " + n.getTitle() + " - " + n.getBody());
                            list.add(n);
                        }
                    TextView emptyView = requireView().findViewById(R.id.textNoNotifications);

                    if (list.isEmpty()) {
                        emptyView.setVisibility(View.VISIBLE);

                    } else {
                        emptyView.setVisibility(View.GONE);

                    }
                    adapter.setNotifications(list);
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (notifListener != null) {
            notifListener.remove();
            notifListener = null;
        }
    }
}