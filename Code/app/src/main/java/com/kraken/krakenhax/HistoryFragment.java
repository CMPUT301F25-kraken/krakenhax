package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Displays the users history, a log of all actions that the user has done or has happened to them.
 */
public class HistoryFragment extends Fragment {
    private Profile currentUser;
    private RecyclerView recyclerView;
    private List<Action> history;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up nav controller
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set up back button
        Button back = view.findViewById(R.id.button_history_back);
        back.setOnClickListener(v -> navController.popBackStack());

        // Get the current user object
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        // Start with empty list to avoid showing stale items
        history = new ArrayList<Action>();

        // Set up the recycler view
        recyclerView = view.findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pass the Activity-owned ViewModels into the adapter
        EventViewModel eventViewModel = null;
        ProfileViewModel profileViewModel = null;
        if (mainActivity != null) {
            eventViewModel = mainActivity.eventViewModel;
            profileViewModel = mainActivity.profileViewModel;
        }
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(history, eventViewModel, profileViewModel);
        recyclerView.setAdapter(adapter);

        // Set up firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        startFirestoreListener(db);
    }

    private void startFirestoreListener(FirebaseFirestore db) {
        if (currentUser == null || currentUser.getID() == null) {
            return;
        }

        Log.d("HistoryCheck", "Listening on profile ID: " + currentUser.getID());


        db.collection("Profiles").document(currentUser.getID())
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        this.currentUser = snapshot.toObject(Profile.class);

                        // --- DIAGNOSTIC LOG ---
                        Log.d("HistoryCheck", snapshot.getData().get("history").toString());
                        if (this.currentUser == null) {
                            Log.e("HistoryCheck", "Deserialized currentUser is null");
                            return;
                        }
                        Log.d("HistoryCheck", "Snapshot ID: " + snapshot.getId());
                        if (this.currentUser.getHistory() == null) {
                            Log.e("HistoryCheck", "History is NULL after Firestore load");
                        } else {
                            Log.d("HistoryCheck", "History size: " + this.currentUser.getHistory().size());
                            for (Action a : this.currentUser.getHistory()) {
                                Log.d("HistoryCheck", "Action ts: " + a.getTimestamp());
                            }
                        }
                        // ----------------------

                        updateRecyclerView();
                    }
                });

    }

    private void updateRecyclerView() {
        if (currentUser == null) return;

        // 1. Get the history list safely
        List<Action> latestHistory = currentUser.getHistory();
        if (latestHistory == null) {
            latestHistory = new java.util.ArrayList<>();
        }

        // 2. Sort history
        latestHistory.sort(Comparator.comparing(Action::getTimestamp, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        // 3. Update the local reference
        this.history = latestHistory;

        // 4. Update the adapter
        if (recyclerView.getAdapter() != null) {
            HistoryRecyclerViewAdapter adapter = (HistoryRecyclerViewAdapter) recyclerView.getAdapter();
            adapter.updateData(this.history);
        } else {
            // If adapter is missing, create it
            MainActivity mainActivity = (MainActivity) getActivity();
            EventViewModel eventViewModel = null;
            ProfileViewModel profileViewModel = null;
            if (mainActivity != null) {
                eventViewModel = mainActivity.eventViewModel;
                profileViewModel = mainActivity.profileViewModel;
            }
            HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(this.history, eventViewModel, profileViewModel);
            recyclerView.setAdapter(adapter);
        }
    }

}