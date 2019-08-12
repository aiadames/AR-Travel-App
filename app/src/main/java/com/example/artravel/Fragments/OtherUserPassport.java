package com.example.artravel.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Activities.ARGemViewer;
import com.example.artravel.GemsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.artravel.R.layout.fragment_other_user_passport;
import static com.example.artravel.R.layout.fragment_passport;

public class OtherUserPassport extends Fragment {
    private RecyclerView rvGems;
    private GemsAdapter adapter;
    private List<Gems> mGems;
    private ImageView profile;
    private ImageView background;
    private TextView username;
    private TextView usernameSub;
    private TextView gemCount;
    private TextView date;
    private ImageButton imageButton;
    private TextView tvScreenName;
    private TextView tvCollectedGemss;
    private ImageButton addFriend;
    private ParseUser user;
    private boolean isFriend = false;


    private static final String TAG = "PassportFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return (View) inflater.inflate(fragment_other_user_passport,container, false);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initalizeUser();
        getActivity().setTitle("Passport");
        checkIfFriend();
        setupView(view);
        queryGems();



    }

    private void queryGems() {

        ParseQuery<Gems> postQuery = new ParseQuery<Gems>(Gems.class);
        // postQuery.include(Gems.KEY_USER);
        if (user == null){
            Toast.makeText(getContext(), "user null", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getContext(), "user " + user.getUsername()+ " is not null", Toast.LENGTH_SHORT).show();
        }

        ParseRelation<Gems> relation;
        relation = user.getRelation("collectedGems");
        relation.getQuery().findInBackground(new FindCallback<Gems>() {
            @Override
            public void done(List<Gems> userGems, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error in query");
                    e.printStackTrace();
                    return;
                }
                mGems.addAll(userGems);

                // Toast.makeText(getContext(), numCollected + " gems collected", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();

            }
        });
    }


    private void setupView(View view) {
        mGems = new ArrayList<>();
        rvGems = view.findViewById(R.id.rvRecyclerView);
        adapter = new GemsAdapter(mGems, getContext());
        rvGems.setAdapter(adapter);
        rvGems.setLayoutManager(new GridLayoutManager(getContext(), 3));

        profile = view.findViewById(R.id.ivPassProfile);
        username = view.findViewById(R.id.tvUsername);
        addFriend = view.findViewById(R.id.btnAddFriend);
        date = view.findViewById(R.id.tvJoinedDate);
        tvScreenName = view.findViewById(R.id.tvScreenName);
        tvCollectedGemss = view.findViewById(R.id.tvCollectedGems);


        Date temp = user.getCreatedAt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String strDate = dateFormat.format(temp);
        date.setText("Joined "+ strDate);

        username.setText(user.get("firstName") + " " + user.get("lastName"));
        tvScreenName.setText("@"+ user.getUsername());
        tvCollectedGemss.setText(user.get("firstName")+"'s Collected Gems");


        ParseFile image = (ParseFile) user.get("image");
        if (image != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions
                    .transforms(new CenterCrop(), new RoundedCorners(400)).format(DecodeFormat.PREFER_ARGB_8888);
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(requestOptions).into(profile);

        }


        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseRelation<ParseUser> friends = currentUser.getRelation("friends");
                if (!isFriend) {
                    isFriend = true;
                    friends.add(user);
                    currentUser.saveInBackground();
                    changeFriendButton();

                } else {
                    isFriend = false;
                    friends.remove(user);
                    currentUser.saveInBackground();
                    changeFriendButton();
                }
            }
        });

    }




    public void initalizeUser(){
        Bundle bundle = this.getArguments();
        user = Parcels.unwrap(bundle.getParcelable("User"));
    }


    public void checkIfFriend(){
        ParseRelation<ParseUser> friends = ParseUser.getCurrentUser().getRelation("friends");
        friends.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    e.printStackTrace();
                } else {
                    for (ParseUser friend: objects){
                        if (friend.getObjectId().equalsIgnoreCase(user.getObjectId())){
                            isFriend = true;
                            changeFriendButton();
                        } else{
                            changeFriendButton();
                        }
                    }
                }
            }
        });
    }


    public void changeFriendButton(){
        if (isFriend == true) {
            addFriend.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.friends));
        } else {
            addFriend.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.add_friend));
        }

    }




}
