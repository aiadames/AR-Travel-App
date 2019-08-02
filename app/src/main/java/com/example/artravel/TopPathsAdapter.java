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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class TopPathsAdapter extends RecyclerView.Adapter<TopPathsAdapter.TopPathsViewHolder> {

    private List<Path> mPaths;
    public Context context;


    public TopPathsAdapter(List<Path> paths) {
        this.mPaths = paths;
    }

    public class TopPathsViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPathImage;
        private TextView mPathName;

        public TopPathsViewHolder(View itemView) {
            super(itemView);
            mPathImage = itemView.findViewById(R.id.ivPathImage);
            mPathName = itemView.findViewById(R.id.tvPathName);


            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment detail = new DetailedPathFragment();
                    int position = getAdapterPosition();
                    Path path = mPaths.get(position);
                    Log.v("yer2", "clicked");

                    // once have grabbed specific path, wrap path object via Parcelable into bundle for next fragment
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Path", Parcels.wrap(path));
                    detail.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, detail).addToBackStack("All paths")
                            .commit();
                }
            });*/
        }

        public void bind(Path myPath) {
            // loading a specific path's data for the RecyclerView display
            mPathName.setText(myPath.getPathName());
            Log.v("yer2", myPath.getPathName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop()).format(DecodeFormat.PREFER_ARGB_8888).override(150,110);
            ParseFile pathImage = myPath.getPathImage();
            if (pathImage != null) {
                Glide.with(context)
                        .load(pathImage.getUrl())
                        .apply(requestOptions).into(mPathImage);
            } else {
                mPathImage.setImageResource(R.drawable.ic_path_placeholder);
            }
        }
    }

    @NonNull
    @Override
    public TopPathsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_path, parent, false);
        context = parent.getContext();
        TopPathsViewHolder tpvh = new TopPathsViewHolder(v);
        return tpvh;
    }

    @Override
    public void onBindViewHolder(@NonNull TopPathsViewHolder holder, int position) {
        final Path currentPath = mPaths.get(position);
        holder.bind(currentPath);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }






}
