package com.kraken.krakenhax;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.MyViewHolder> {
    private final List<Action> data;
    private ItemClickListener clickListener;

    public HistoryRecyclerViewAdapter(List<Action> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public HistoryRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recycler_view_row, parent, false);
        return new HistoryRecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewAdapter.MyViewHolder holder, int position) {
        Action action = data.get(position);

        // Set the timestamp
        Timestamp timestamp = action.getTimestamp();
        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy, hh:mm a", Locale.getDefault());
        String formattedTimestamp = formatter.format(timestamp.toDate());
        holder.tvActionTitle.setText(formattedTimestamp);

        // Set the action description
        holder.tvActionDescription.setText(action.getAction() + " Related event ID: " + action.getAssociatedEventID() + " Related profile ID: " + action.getAssociatedUserID());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Action> newData) {
        this.data.clear();
        this.data.addAll(newData);
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public Action getItem(int id) {
        return data.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView tvActionTitle;
        final TextView tvActionDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvActionTitle = itemView.findViewById(R.id.tv_action_title);
            tvActionDescription = itemView.findViewById(R.id.tv_action_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(view, position);
                }
            }
        }
    }

}
