package com.kraken.krakenhax;

import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MyRecyclerViewAdapter adapter;

    public EventsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container);

        // Set up recycler view
        RecyclerView recycler_view_event_list = view.findViewById(R.id.recycler_view_events_list);
        recycler_view_event_list.setLayoutManager(new LinearLayoutManager(requireContext()));

        ArrayList<Event> demo_list = new ArrayList<>();
        demo_list.add(new Event("Event 1"));
        demo_list.add(new Event("Event 2"));
        demo_list.add(new Event("Event 3"));
        demo_list.add(new Event("Event 4"));
        demo_list.add(new Event("Event 5"));
        demo_list.add(new Event("Event 6"));
        demo_list.add(new Event("Event 7"));
        demo_list.add(new Event("Event 8"));

        adapter = new MyRecyclerViewAdapter(demo_list);

        // Set an on item click listener for the recycler view
        adapter.setClickListener(new MyRecyclerViewAdapter.ItemClickListener() {
            // When an event is clicked on
            @Override
            public void onItemClick(View view, int position) {
                Event clickedEvent = adapter.getItem(position);
                Log.d("EventsFragment", "You clicked " + clickedEvent.getTitle() + " on row number " + position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("event_name", clickedEvent);
                navController.navigate(R.id.action_EventsFragment_to_EventFragment, bundle);
            }
        });

        recycler_view_event_list.setAdapter(adapter);

        //adapter.updateData(demo_list);

    }

}