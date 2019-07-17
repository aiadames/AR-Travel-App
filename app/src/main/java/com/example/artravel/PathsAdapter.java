package com.example.artravel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.models.Path;

import java.util.List;

public class PathsAdapter extends RecyclerView.Adapter<PathsAdapter.PathsViewHolder> {


    private List<Path> mPathList;

    public static class PathsViewHolder extends RecyclerView.ViewHolder{
        public ImageView mPathImage;
        public TextView mPathTitle;
        public TextView mPathDescription;

        public PathsViewHolder(View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathTitle = itemView.findViewById(R.id.tvPathTitle);
            mPathDescription = itemView.findViewById(R.id.tvPathDescription);
        }
    }

    public PathsAdapter(List<Path> pathList){
        mPathList = pathList;
    }

    @NonNull
    @Override
    public PathsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_path, parent, false);
        PathsViewHolder pvh = new PathsViewHolder(v);
        return pvh;

    }

    @Override
    public void onBindViewHolder(@NonNull PathsViewHolder holder, int position) {
        Path currentPath = mPathList.get(position);
        holder.mPathTitle.setText(currentPath.getPathTitle());
        holder.mPathDescription.setText(currentPath.getPathDescription());
    }

    @Override
    public int getItemCount() {
        return mPathList.size();
    }
}
