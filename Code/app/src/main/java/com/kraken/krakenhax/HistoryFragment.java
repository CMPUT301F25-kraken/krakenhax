package com.kraken.krakenhax;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

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

        // Get the current user object
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            currentUser = mainActivity.currentUser;
        }

        // Set up firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        startFirestoreListener(db);

        // Get the history list from current user
        history = currentUser.getHistory();

        // Set up the recycler view
        recyclerView = view.findViewById(R.id.history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(history);
        recyclerView.setAdapter(adapter);
    }

    private void startFirestoreListener(FirebaseFirestore db) {
        if (currentUser == null || currentUser.getID() == null) {
            return;
        }

//        db.collection("Profiles").document(currentUser.getID())
//                .addSnapshotListener((snapshot, e) -> {
//                    if (snapshot != null && snapshot.exists()) {
//                        // Update the current user instance
//                        this.currentUser = snapshot.toObject(Profile.class);
//                        // Update the UI
//                        updateRecyclerView();
//                    }
//                });

        db.collection("Profiles").document(currentUser.getID())
                .addSnapshotListener((snapshot, e) -> {
                    if (snapshot != null && snapshot.exists()) {
                        this.currentUser = snapshot.toObject(Profile.class);

                        // --- DIAGNOSTIC LOG ---
                        if (this.currentUser.getHistory() == null) {
                            android.util.Log.e("HistoryCheck", "History is NULL after Firestore load");
                        } else {
                            android.util.Log.d("HistoryCheck", "History size: " + this.currentUser.getHistory().size());
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

        // 2. Update the local reference
        this.history = latestHistory;

        // 3. Update the adapter
        if (recyclerView.getAdapter() != null) {
            HistoryRecyclerViewAdapter adapter = (HistoryRecyclerViewAdapter) recyclerView.getAdapter();
            adapter.updateData(this.history);
        } else {
            // If adapter is missing, create it
            HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(this.history);
            recyclerView.setAdapter(adapter);
        }
    }

//    private void updateRecyclerView() {
//        // 1. Safety Check: Ensure user and history exist
//        if (this.currentUser == null) return;
//
//        // 2. Get the history list (handle null case by creating empty list)
//        if (this.currentUser.getHistory() == null) {
//            this.history = new java.util.ArrayList<>();
//        } else {
//            this.history = this.currentUser.getHistory();
//        }
//
//        // 3. Update Adapter
//        if (recyclerView != null && recyclerView.getAdapter() != null) {
//            HistoryRecyclerViewAdapter adapter = (HistoryRecyclerViewAdapter) recyclerView.getAdapter();
//            adapter.updateData(this.history);
//        } else if (recyclerView != null) {
//            // Fallback: If adapter was null for some reason, create it now
//            HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(this.history);
//            recyclerView.setAdapter(adapter);
//        }
//    }


}