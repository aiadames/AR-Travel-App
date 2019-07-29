package com.example.artravel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

public class CompletedPathsAdapter extends RecyclerView.Adapter<CompletedPathsAdapter.PathsViewHolder> {


    private List<Path> mPathList;
    public Context context;
    private List<Path> relation_Paths;


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
        holder.bind(currentPath);
        holder.setIsRecyclable(false);

    }

    public CompletedPathsAdapter(List<Path> pathList) {
        mPathList = pathList;
    }

    public class PathsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPathImage;
        private TextView mPathTitle;
        private TextView mPathDescription;

        public PathsViewHolder(View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathTitle = itemView.findViewById(R.id.tvPathTitle);
            mPathDescription = itemView.findViewById(R.id.tvPathDescription);
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


    @Override
    public int getItemCount() {
        return mPathList.size();
    }




}
