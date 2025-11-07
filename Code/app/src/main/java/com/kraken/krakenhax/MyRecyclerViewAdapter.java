package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Creates an adapter for the RecyclerView, allows us to connect our RecyclerView to an array containing the data we want to display.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {
    private final ArrayList<Event> data;
    private ItemClickListener clickListener;

    /**
     * Constructor for the adapter.
     * @param data The list of events to be displayed.
     */
    public MyRecyclerViewAdapter(ArrayList<Event> data) {
        this.data = data;
    }

    /**
     * Called when RecyclerView needs a new {@link MyViewHolder} to represent an item.
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new MyViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        return new MyRecyclerViewAdapter.MyViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.MyViewHolder holder, int position) {
        Event event = data.get(position);
        holder.tvLogEntry.setText(event.getTitle());
        holder.tvDescription.setText(event.getEventDetails());
        String poster = event.getPoster();
        if (poster == null || poster.isEmpty()) {
            holder.ivEventImage.setImageResource(R.drawable.outline_attractions_100);
        } else {
            Picasso.get()
                    .load(poster)
                    .placeholder(R.drawable.outline_attractions_100)
                    .error(R.drawable.outline_attractions_100)
                    .fit().centerCrop()
                    .into(holder.ivEventImage);
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Updates the data set of the adapter.
     * @param newData The new list of events to display.
     */
    public void updateData(ArrayList<Event> newData) {
        this.data.clear();
        this.data.addAll(newData);
        notifyDataSetChanged();
    }

    /**
     * Sets the click listener for the items in the RecyclerView.
     * @param itemClickListener The listener to be set.
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * Gets the event at a specific position.
     * @param id The position of the item.
     * @return The event at the specified position.
     */
    public Event getItem(int id) {
        return data.get(id);
    }

    /**
     * Interface for handling click events on RecyclerView items.
     */
    public interface ItemClickListener {
        /**
         * Called when an item in the RecyclerView is clicked.
         * @param view The view that was clicked.
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int position);
    }

    /**
     * Holds an TextView item for an individual row of the RecyclerView.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvLogEntry;

        final TextView tvDescription;
        final ImageView ivEventImage;

        /**
         * Constructor for the MyViewHolder.
         * @param itemView The view for a single row in the list.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLogEntry = itemView.findViewById(R.id.tv_event_title);
            ivEventImage = itemView.findViewById(R.id.event_icon);
            tvDescription = itemView.findViewById(R.id.tv_description);

            itemView.setOnClickListener(this);
        }

        /**
         * Called when a view has been clicked.
         * @param view The View that was clicked.
         */
        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}