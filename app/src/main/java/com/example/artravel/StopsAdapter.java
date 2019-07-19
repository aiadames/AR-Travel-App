package com.example.artravel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.R;
import com.example.artravel.models.Stop;
import com.parse.ParseException;

import java.util.Collections;
import java.util.List;

public class StopsAdapter extends
        RecyclerView.Adapter<StopsAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Stop> mStops;

    public StopsAdapter(List<Stop> stops) {
        mStops = stops;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_stop, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stop stop = mStops.get(position);

        String stopName = "";
        try {
            stopName = stop.fetchIfNeeded().getString("stopName");
        } catch (ParseException e) {
            Log.e("StopsAdapter", "Something has gone terribly wrong with Parse", e);
        }
        holder.tvStopName.setText(stopName);
    }

    @Override
    public int getItemCount() {
        return mStops.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mStops, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mStops, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvStopName;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            tvStopName = itemView.findViewById(R.id.tvStopName);
        }
    }
}