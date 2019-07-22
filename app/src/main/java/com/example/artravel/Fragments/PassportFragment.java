package com.example.artravel.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.GemsAdapter;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.artravel.R.layout.fragment_passport;

public class PassportFragment extends Fragment {
    private RecyclerView rvGems;
    private GemsAdapter adapter;
    private List<Gems> mGems;

    private ImageView profile;
    private TextView username;
    private TextView gemCount;
    private Context context;
    private static final String TAG = "PassportFragment";

    //  private Button btnTest;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_passport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);
        queryGems();
        setView(view);
        //TODO
        //Set onclick listener for profile fragmemnt

    }

    private void queryGems() {

        ParseQuery<Gems> postQuery = new ParseQuery<Gems>(Gems.class);
        // postQuery.include(Gems.KEY_USER);
        postQuery.orderByDescending("createdAt");
        postQuery.findInBackground(new FindCallback<Gems>() {

            @Override
            public void done(List<Gems> posts, ParseException e) {

                if (e != null) {
                    Log.e(TAG, "error in query");
                    e.printStackTrace();
                    return;
                }
                mGems.addAll(posts);
                adapter.notifyDataSetChanged(); }
        }
        );
    }

    private void setupView(View view) {

        mGems = new ArrayList<>();
        rvGems = view.findViewById(R.id.rvRecyclerView);
        adapter = new GemsAdapter(mGems, getContext());
        rvGems.setAdapter(adapter);

        rvGems.setLayoutManager(new GridLayoutManager(getContext(), 3));

        username = view.findViewById(R.id.tvUsername);
        gemCount = view.findViewById(R.id.tvPrompt);
        profile = view.findViewById(R.id.ivProfileImag1);

    }

    private void setView(View view) {
        //TODO
        //need to setup user gem store and return to set user number of gems

        ParseUser user = ParseUser.getCurrentUser();
        if (user == null)
            Toast.makeText(view.getContext(), "user null", Toast.LENGTH_SHORT).show();

        username.setText(user.getUsername());
        gemCount.setText("You've collected " + user.getUsername() + " gems this week");

        ParseFile image = (ParseFile) user.get("image");
        if (image != null) {
            Toast.makeText(view.getContext(),"image being loaded", Toast.LENGTH_SHORT).show();
            Glide.with(view.getContext())
                    .load(image.getUrl())
                    // .bitmapTransform(new RoundedCornersTransformation(context, 25, 0))
                    .into(profile);

        }
        else
            Toast.makeText(view.getContext(),"Failed image null", Toast.LENGTH_SHORT).show();

    }


}
