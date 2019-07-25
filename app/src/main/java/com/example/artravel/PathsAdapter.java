package com.example.artravel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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



    public class PathsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPathImage;
        private TextView mPathTitle;
        private TextView mPathDescription;

        public PathsViewHolder(View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathTitle = itemView.findViewById(R.id.tvPathTitle);
            mPathDescription = itemView.findViewById(R.id.tvPathDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment detail = new DetailedPathFragment();

                    int position = getAdapterPosition();
                    Path path = mPathList.get(position);

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

//    public void relation_Check(Path currentPath) {
//
//        ParseUser user = ParseUser.getCurrentUser();
//        ParseRelation<Path> relation;
//        relation = user.getRelation("startedPaths");
//        //relation_Paths= new ArrayList<>();
//
//        relation.getQuery().findInBackground(new FindCallback<Path>() {
//            @Override
//            public void done(List<Path> userPaths, ParseException e) {
//                if (e != null) {
//                    Toast.makeText(context, "query error", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                    return ;
//                }
//                helper(userPaths);
//                Log.d("Relations", "relation size" + relation_Paths.size());
//
//                //Log.d("Relations", "relation size" + relation_Paths.size());
//
//            }
//        });

   //     }
//    private int helper( List<Path> userPaths){
//        relation_Paths.clear();
//        relation_Paths.addAll(userPaths);
//        return relation_Paths.size();
//        }

        // Toast.makeText(context, "hello" + started_check + "hrllo", Toast.LENGTH_SHORT).show();
//started check was added to signal that a path was in the users started paths


    @Override
    public void onBindViewHolder(@NonNull PathsViewHolder holder, int position) {
        final Path currentPath = mPathList.get(position);


        holder.bind(currentPath);

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
