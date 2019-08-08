package com.example.artravel;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.Fragments.OtherUserPassport;
import com.example.artravel.Fragments.PassportFragment;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> implements Filterable {

    private List<ParseUser> mUsersList;
    private List<ParseUser> mUsersListFull;
    public Context context;
    public int friendCount = 0;


    public class UsersViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivUserProfilePicture;
        private TextView tvUserScreenName;
        private TextView tvUserName;



        public UsersViewHolder(View itemView){
            super(itemView);
            ivUserProfilePicture = itemView.findViewById(R.id.ivUserProfilePic);
            tvUserScreenName = itemView.findViewById(R.id.tvUserScreenName);
            tvUserName = itemView.findViewById(R.id.tvUserName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment passport = new OtherUserPassport();
                    int position = getAdapterPosition();
                    ParseUser user = mUsersList.get(position);
                    // once have grabbed specific path, wrap path object via Parcelable into bundle for next fragment
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("User", Parcels.wrap(user));
                    passport.setArguments(bundle);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, passport).addToBackStack("Users")
                            .commit();
                }
            });

        }


        public void bind(ParseUser user){
            Random rand = new Random();
            int value = rand.nextInt(999);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CircleCrop()).format(DecodeFormat.PREFER_ARGB_8888);
            tvUserScreenName.setText("@"+user.getUsername());
            tvUserName.setText(user.get("firstName") + " "+ user.get("lastName"));
            if (user.get("image") != null){
                Glide.with(context).load(((ParseFile)(user.get("image"))).getUrl()).apply(requestOptions).into(ivUserProfilePicture);
            } else{
                ivUserProfilePicture.setImageResource(R.drawable.ic_person_icon);
            }



        }
    }


    public UsersAdapter(List<ParseUser> userList, List<ParseUser> pathListFull ){
        this.mUsersList = userList;
        mUsersListFull = pathListFull;
    }



    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        context = parent.getContext();
        UsersViewHolder pvh = new UsersViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        final ParseUser user = mUsersList.get(position);
        holder.bind(user);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }


    @Override
    public Filter getFilter() {
        return userFilter;
    }


    private Filter userFilter = new Filter(){
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // create a new list which will include all our filtered results
            List<ParseUser> filteredList = new ArrayList<>();
            // if there is no search constraint/is empty "": add all items already in mPathListFull
            // else: grab the filter constraint based on text change, then iterate through all paths (mPathListFull)
            // and grab each path's title as a String to see if it contains our filter, if so add to list
            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(mUsersListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ParseUser searchUser: mUsersListFull){
                    if (searchUser.getUsername().toLowerCase().contains(filterPattern)){
                        filteredList.add(searchUser);
                    }
                }
            }
            // return filtered list values stored in a filter result
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mUsersList.clear();
            mUsersList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };





}
























