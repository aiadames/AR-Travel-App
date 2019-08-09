package com.example.artravel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.Fragments.OtherUserPassport;
import com.example.artravel.Fragments.SearchFriendsFragment;
import com.example.artravel.models.Path;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class MyFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<ParseUser> myFriends;
    public Context context;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;


    public MyFriendsAdapter(List<ParseUser> friendsList){
        this.myFriends = friendsList;
    }


    public class MyFriendsViewHolder extends RecyclerView.ViewHolder {
        private ImageView friendProfile;
        private TextView friendScreenName;
        private TextView friendName;


        public MyFriendsViewHolder(View itemView){
            super(itemView);
            friendProfile = itemView.findViewById(R.id.ivProfile);
            friendScreenName = itemView.findViewById(R.id.tvScreenName);
            friendName = itemView.findViewById(R.id.tvAddFriends);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    final ParseUser myFriend = myFriends.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("User", Parcels.wrap(myFriend));
                    OtherUserPassport passport= new OtherUserPassport();
                    passport.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, passport).addToBackStack("home")
                            .commit();
                }
            });
        }








    }

    public class AddFriendsViewHolder extends RecyclerView.ViewHolder {
        private ImageButton ibAdd;
        private TextView tvAddFriends;

        public AddFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            ibAdd = itemView.findViewById(R.id.ibAdd);
            tvAddFriends = itemView.findViewById(R.id.tvAddFriends);
            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SearchFriendsFragment search = new SearchFriendsFragment();
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, search).addToBackStack("home")
                            .commit();
                }
            });

        }

    }




    @Override
    public int getItemViewType(int position) {
        ParseUser user = myFriends.get(position);
        if (user.getUsername() != null){
            return TYPE_ONE;
        } else{
            return TYPE_TWO;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ONE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_friend, parent, false);
            context = parent.getContext();
            MyFriendsViewHolder mfvh = new MyFriendsViewHolder(v);
            return mfvh;
        } else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friends, parent, false);
            context = parent.getContext();
            AddFriendsViewHolder afvh = new AddFriendsViewHolder(v);
            return afvh;
        }

    }


        @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ONE:
                initLayoutOne((MyFriendsViewHolder) holder, position);
                break;
            case TYPE_TWO:
                initLayoutTwo((AddFriendsViewHolder) holder, position);
                break;
            default:
                break;
        }



    }

    private void initLayoutOne(MyFriendsViewHolder holder, int pos) {
        holder.setIsRecyclable(false);
        final ParseUser myFriend = myFriends.get(pos);
        holder.friendScreenName.setText("@" + myFriend.getUsername());
        holder.friendName.setText(myFriend.get("firstName") + " " + myFriend.get("lastName"));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CircleCrop()).format(DecodeFormat.PREFER_ARGB_8888).override(100,100);
        Glide.with(context).load(((ParseFile)(myFriend.get("image"))).getUrl()).apply(requestOptions).into(holder.friendProfile);

    }

    private void initLayoutTwo(AddFriendsViewHolder holder, int pos) {

    }


    @Override
    public int getItemCount() {
        return myFriends.size();
    }
}
