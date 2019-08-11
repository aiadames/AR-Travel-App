package com.example.artravel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.models.Gems;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class GemsAdapter extends RecyclerView.Adapter<GemsAdapter.GemsViewHolder> {
    private List<Gems> gemsList;
    public Context context;

    public ImageView gemImage;

    public GemsAdapter(List<Gems> gemsListNew, Context context) {
        gemsList = gemsListNew;
        this.context= context;
    }

    public class GemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public GemsViewHolder(View itemView) {
            super(itemView);
            gemImage = itemView.findViewById(R.id.ivArGemImage);
            itemView.setOnClickListener(this);

        }

        public void bind(Gems myGem) {
           //gemName.setText(myGem.getName());
            int pos = getAdapterPosition();
            ParseFile image = myGem.getImage();

                    if (image != null) {
                        Glide.with(context)
                                .load(image.getUrl())
                                //.apply(new RequestOptions().transforms(new RoundedCorners(100)))
                                .into(gemImage);
                    }

                }

        public void onClick(View view) {

            int position = getAdapterPosition();

            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position, this won't work if the class is static
                Gems gem = gemsList.get(position);
                //.makeText(context, gem.getObjectId(), Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putParcelable("Gems", Parcels.wrap(gem));
                Intent intent = new Intent(view.getContext(), GemDetail.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);

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
