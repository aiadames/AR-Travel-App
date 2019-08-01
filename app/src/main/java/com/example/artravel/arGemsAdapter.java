package com.example.artravel;


import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.artravel.models.Gems;
import com.parse.ParseFile;

import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

//this adapter is the same as the one used in passport with a few changes
//the onclick listener puts the object into sceneform
//the xml file for the gem cardview is different

public class arGemsAdapter extends RecyclerView.Adapter<arGemsAdapter.GemsViewHolder> {
    private List<Gems> gemsList;
    public Context context;
    public Vibrator vibrator;
    public ImageView gemImage;
    public int selection;
    public CardView cardView;
    public String imageLink;
    public RecyclerView recyclerView;

    public arGemsAdapter(List<Gems> gemsListNew, Context context) {
        gemsList = gemsListNew;
        this.context= context;
    }

    public class GemsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        public GemsViewHolder(View itemView) {
            super(itemView);
            gemImage = itemView.findViewById(R.id.ivArGemImage);
            cardView = itemView.findViewById(R.id.cardViewGem);
            itemView.setOnClickListener(this);

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

        public void onClick(View view) {
            vibrator = (Vibrator) view.getContext().getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(15);


            int position = getAdapterPosition();
            selection = position;
            //cardView.setCardBackgroundColor(0xffffff00);
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
    }

    @Override
    public int getItemCount() {
        return gemsList.size();
    }

    public int getSelected(){
        return selection;

    }

    public String getImageLink(){
        Gems currentGem = gemsList.get(getSelected());

       imageLink= currentGem.getModel();
       return imageLink;

    }



}
