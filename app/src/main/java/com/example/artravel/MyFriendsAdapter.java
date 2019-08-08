package com.example.artravel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.models.Path;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class MyFriendsAdapter extends RecyclerView.Adapter<MyFriendsAdapter.MyFriendsViewHolder> {

    public List<ParseUser> myFriends;
    public Context context;

    public MyFriendsAdapter(List<ParseUser> friendsList){
        this.myFriends = friendsList;
    }


    public class MyFriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView friendProfile;
        private TextView friendScreenName;
        private TextView friendName;


        public MyFriendsViewHolder(View itemView){
            super(itemView);
            friendProfile = itemView.findViewById(R.id.ivProfile);
            friendScreenName = itemView.findViewById(R.id.tvScreenName);
            friendName = itemView.findViewById(R.id.tvName);


        }

        public void bind(ParseUser friend){
            friendScreenName.setText("@" + friend.getUsername());
            friendName.setText(friend.get("firstName") + " " + friend.get("lastName"));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CircleCrop()).format(DecodeFormat.PREFER_ARGB_8888).override(100,100);
            Glide.with(context).load(((ParseFile)(friend.get("image"))).getUrl()).apply(requestOptions).into(friendProfile);

        }




        @Override
        public void onClick(View view) {

        }
    }



    @NonNull
    @Override
    public MyFriendsAdapter.MyFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_friend, parent, false);
        context = parent.getContext();
        MyFriendsViewHolder mfvh = new MyFriendsViewHolder(v);
        return mfvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyFriendsAdapter.MyFriendsViewHolder holder, int position) {
        final ParseUser myFriend = myFriends.get(position);
        holder.bind(myFriend);
        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return myFriends.size();
    }
}
