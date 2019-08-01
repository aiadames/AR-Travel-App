package com.example.artravel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;


import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class PathsAdapter extends RecyclerView.Adapter<PathsAdapter.PathsViewHolder> implements Filterable {


    private List<Path> mPathList;
    private List<Path> mPathListFull;
    public Context context;
    private int started_check ;
    private List<Path> relation_Paths;
    ConstraintLayout relativeLayout;



    public class PathsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPathImage;
        private TextView mPathTitle;
        private TextView mPathDescription;

        public PathsViewHolder(View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathTitle = itemView.findViewById(R.id.tvPathTitle);
            mPathDescription = itemView.findViewById(R.id.tvPathDescription);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment detail = new DetailedPathFragment();
                    int position = getAdapterPosition();
                    Path path = mPathList.get(position);

                    // once have grabbed specific path, wrap path object via Parcelable into bundle for next fragment
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Path", Parcels.wrap(path));
                    detail.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, detail).addToBackStack("All paths")
                            .commit();
                }
            });
        }

        public void bind(Path myPath) {
            // loading a specific path's data for the RecyclerView display
            mPathDescription.setText(myPath.getPathDescription());
            mPathTitle.setText(myPath.getPathName());
            ParseFile pathImage = myPath.getPathImage();
            if (pathImage != null) {
                Glide.with(context).load(pathImage.getUrl()).into(mPathImage);
            } else {
                mPathImage.setImageResource(R.drawable.ic_path_placeholder);
            }
        }
    }

    public PathsAdapter(List<Path> pathList, List<Path> pathListFull){
        this.mPathList = pathList;
        // mPathListFull is an independent list, don't point to same list (mutability prevention) but stores all paths
        // will be used for filter as can iterate through all paths and only update mPaths which is bound to the layout
        mPathListFull = pathListFull;
    }

    @NonNull
    @Override
    public PathsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_path, parent, false);
        context = parent.getContext();
        PathsViewHolder pvh = new PathsViewHolder(v);
        return pvh;

    }


    @Override
    public void onBindViewHolder(@NonNull PathsViewHolder holder, int position) {
        final Path currentPath = mPathList.get(position);
        for (int i = 0; i < mPathListFull.size() ; i++) {
            Log.d("Testaa", mPathListFull.get(i).getPathName());
            Log.d("Testaa", mPathListFull.get(i).getStartedPath() ? "true" : "false");
        }
        holder.bind(currentPath);
        holder.setIsRecyclable(false);

        // based on if path is started or completed, change the display color so users can easily determine paths they can access
        if (currentPath.getStartedPath() == true) {
            relativeLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.inProgressBlue));
        }else if (currentPath.getCompletedPath() == true){
            relativeLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.grey));
        }
    }

    @Override
    public int getItemCount() {
        return mPathList.size();
    }

    @Override
    public Filter getFilter() {
        return pathFilter;
    }

    private Filter pathFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            // create a new list which will include all our filtered results
            List<Path> filteredList = new ArrayList<>();
            // if there is no search constraint/is empty "": add all items already in mPathListFull
            // else: grab the filter constraint based on text change, then iterate through all paths (mPathListFull)
            // and grab each path's title as a String to see if it contains our filter, if so add to list
            if (constraint == null || constraint.length() == 0){
                  filteredList.addAll(mPathListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Path item: mPathListFull){
                    if (item.getPathName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            // return filtered list values stored in a filter result
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            // display filtered results to screen via clearing mPathList and adding values stored
            mPathList.clear();
            mPathList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };



}
