package com.example.artravel;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artravel.Activities.ARGemViewer;
import com.example.artravel.models.Gems;
import com.parse.ParseFile;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

public class arGemsAdapter extends RecyclerView.Adapter<arGemsAdapter.GemsViewHolder> {
    private List<Gems> gemsList;
    public Context context;
    public Vibrator vibrator;
    public ImageView gemImage;
    public int selection = -1;
    public CardView cardView;
    public String imageLink;
    public int position;
    private OnItemClickListener listener;

    public arGemsAdapter(List<Gems> gemsListNew, Context context) {
        gemsList = gemsListNew;
        this.context= context;
    }


    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public class GemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public GemsViewHolder(final View itemView) {
            super(itemView);
            gemImage = itemView.findViewById(R.id.ivArGemImage);
            cardView = itemView.findViewById(R.id.cardViewGem);
            itemView.setOnClickListener(this);

            //handle on click in the parent method instead of the adapter
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });

        }

        public void bind(Gems myGem) {

            ParseFile image = myGem.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        //.apply(RequestOptions.circleCropTransform())
                        .into(gemImage);
            }
        }

        @Override
        public void onClick(View view) {

        }
    }
    @Override
    public GemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ar_gem, parent, false);
        GemsViewHolder viewHolder = new GemsViewHolder(view);
        return viewHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull GemsViewHolder holder, int position) {
        Gems currentGem = gemsList.get(position);
        holder.bind(currentGem);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return gemsList.size();
    }

    public int getSelected(){
       // Toast.makeText(recyclerView.getContext(), "selected inside of get selected" + selection, Toast.LENGTH_SHORT).show();
        return( selection!= -1)? selection:0;


    }

    public String getImageLink(int selection){
        Gems currentGem = gemsList.get(selection);
//        Toast.makeText(recyclerView.getContext(), "selected inside of get image link " + selection, Toast.LENGTH_SHORT).show();
       imageLink= currentGem.getModel();
       return imageLink;

    }


}