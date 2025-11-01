package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Creates an adapter for the RecyclerView, allows us to connect our RecyclerView to an array containing the data we want to display.
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private final ArrayList<Event> data;
    private ItemClickListener clickListener;

    public MyRecyclerViewAdapter(ArrayList<Event> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row, parent, false);
        return new MyRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tvLogEntry.setText(data.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(ArrayList<Event> newData) {
        this.data.clear();
        this.data.addAll(newData);
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public Event getItem(int id) {
        return data.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * Holds an TextView item for an individual row of the RecyclerView.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvLogEntry;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvLogEntry = itemView.findViewById(R.id.tv_event_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}