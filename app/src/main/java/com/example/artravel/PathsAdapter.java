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

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Path", Parcels.wrap(path));
                    detail.setArguments(bundle);

                    /*ParseRelation<Path> completedPaths = ParseUser.getCurrentUser().getRelation("completedPaths");
                    completedPaths.getQuery().findInBackground(new FindCallback<Path>() {
                        @Override
                        public void done(List<Path> objects, ParseException e) {
                            if (e != null){
                                e.printStackTrace();
                            } else {
                                for (int i = 0; i < objects.size(); i++) {
                                    if (objects.get(i).getObjectId().equals(path.getObjectId())) {

                                    }
                                }
                            }
                        }
                    });
                    */

                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, detail).addToBackStack("All paths")
                            .commit();


                }
            });
        }

        public void bind(Path myPath) {
            mPathDescription.setText(myPath.getPathDescription());
           // String temp = myPath.getStartedPath() == true ? "true" : "false";
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
        mPathListFull = pathListFull;// independent list, don't point to same list (mutability prevention)
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

        if (currentPath.getStartedPath() == true) {
            Log.d("test", "change color");
            relativeLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.green));
        }else if (currentPath.getCompletedPath() == true){
            relativeLayout.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
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
            List<Path> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                  filteredList.addAll(mPathListFull);   // add all items
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Path item: mPathListFull){
                    if (item.getPathName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mPathList.clear();
            mPathList.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    };



}
