package com.example.artravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
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
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Fragments.GemDetail;
import com.example.artravel.Fragments.PassportFragment;
import com.example.artravel.models.Gems;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;


public class GemsAdapter extends RecyclerView.Adapter<GemsAdapter.GemsViewHolder> {
    private List<Gems> gemsList;
    public Context context;

    public ImageView gemImage;
    public TextView gemName;

    public GemsAdapter(List<Gems> gemsListNew, Context context) {
        gemsList = gemsListNew;
        this.context= context;
    }

    public class GemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public GemsViewHolder(View itemView) {
            super(itemView);
            gemImage = itemView.findViewById(R.id.ivImage);
            gemName = itemView.findViewById(R.id.tvName);
            itemView.setOnClickListener(this);

        }

        public void bind(Gems myGem) {
            gemName.setText(myGem.getName());

            ParseFile image = myGem.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(gemImage);
            }
        }

        public void onClick(View view) {

            int position = getAdapterPosition();

            Fragment details = new GemDetail();

            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Gems gem = gemsList.get(position);
                Toast.makeText(context, gem.getObjectId(), Toast.LENGTH_SHORT).show();


                Bundle bundle = new Bundle();
                bundle.putParcelable("Gems", Parcels.wrap(gem));
                details.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, details)
                        .commit();
            }
        }
    }
        @Override
        public GemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gem, parent, false);
            GemsViewHolder viewHolder = new GemsViewHolder(view);
            return viewHolder;

        }


        @Override
        public void onBindViewHolder(@NonNull GemsViewHolder holder, int position) {
            Gems currentGem = gemsList.get(position);
            holder.bind(currentGem);
        }

        @Override
        public int getItemCount() {
            return gemsList.size();
        }



}
