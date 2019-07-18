package com.example.artravel;

import android.content.Context;
import android.os.Bundle;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.models.Path;


import org.parceler.Parcels;

import java.util.List;

public class PathsAdapter extends RecyclerView.Adapter<PathsAdapter.PathsViewHolder> {


    private List<Path> mPathList;
    public Context context;

    public class PathsViewHolder extends RecyclerView.ViewHolder{
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
                    Toast.makeText(view.getContext(),"clicked on path", Toast.LENGTH_SHORT).show();
                    Fragment detail = new DetailedPathFragment();

                    int position = getAdapterPosition();
                    Path path = mPathList.get(position);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Path", Parcels.wrap(path));
                    detail.setArguments(bundle);

                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, detail)
                            .commit();


                }
            });
        }
        public void bind(Path myPath) {
            mPathDescription.setText(myPath.getPathDescription());
            mPathTitle.setText(myPath.getPathName());


        }
    }

    public PathsAdapter(List<Path> pathList){
        mPathList = pathList;
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
        Path currentPath = mPathList.get(position);
        holder.bind(currentPath);
    }

    @Override
    public int getItemCount() {
        return mPathList.size();
    }



}
