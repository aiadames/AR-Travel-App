package com.example.artravel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Fragments.StopDetailsFragment;
import com.example.artravel.Fragments.StopFragment;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class  StopsAdapter extends
        RecyclerView.Adapter<StopsAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Stop> mStops;
    public Context context;
    private Bundle fragmentBundle;

    public StopsAdapter(List<Stop> stops, Context context, Bundle data) {
        mStops = stops;
        this.context= context;
        fragmentBundle = data;
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

        String stopName = getNameOfStop(stop);
        holder.tvStopName.setText(stopName);

        ParseFile image = stop.getStopImage();
        if (image != null) {
            Glide.with(context)
                    .load(image.getUrl())
                    .into(holder.ivStopImage);
        }
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
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvStopName;
        public ImageView ivStopImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvStopName = itemView.findViewById(R.id.tvStopName);
            ivStopImage = itemView.findViewById(R.id.ivStopImage);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Stop clickedStop = mStops.get(position);
                Fragment stopFragment = new StopFragment();

                // Get bundle containing path and stops
                Path path = Parcels.unwrap(fragmentBundle.getParcelable("Path"));
                ArrayList<Stop> stops = Parcels.unwrap(fragmentBundle.getParcelable("Stops Array"));
                int stopIndex = fragmentBundle.getInt("Stop Index");

                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null && path.getCompletedPath() == false) {
                    ParseRelation<Path> relation = currentUser.getRelation("startedPaths");
                    relation.add(path);
                    currentUser.saveInBackground();

                    // Create new bundle also containing the stop that was clicked
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Stop", Parcels.wrap(clickedStop));
                    bundle.putParcelable("Path", Parcels.wrap(path));
                    bundle.putParcelable("Stops Array", Parcels.wrap(stops));
                    bundle.putInt("Stop Index", stopIndex);

                    stopFragment.setArguments(bundle);

                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, stopFragment).addToBackStack("Path Detail")
                            .commit();
                } else if (path.getCompletedPath() == true) {
                    Toast.makeText(context, "You have already completed this path", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String getNameOfStop(Stop stop) {
        String stopName = "";
        try {
            stopName = stop.fetchIfNeeded().getString("stopName");
        } catch (ParseException e) {
            Log.e("StopsAdapter", "Unable to query stop name from Parse", e);
        }
        return stopName;
    }

}