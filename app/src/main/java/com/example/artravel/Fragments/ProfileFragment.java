package com.example.artravel.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.MainActivity;
import com.example.artravel.Manifest;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;


import static com.example.artravel.R.layout.fragment_profile;




public class ProfileFragment extends Fragment {

    private ImageButton ibProfile;
    private TextView tvName;
    private TextView tvWelcome;
    public static final int GET_FROM_GALLERY = 3;
    private ParseUser currentUser;

    private TextView tvUsername;
    private TextView tvUserName;
    private TextView tvEmail;
    private TextView tvUserEmail;
    private TextView tvCompletedPaths;
    private Button btnViewCompleted;

    private Button btnLogOut;

    public static final int STORAGE_PERMISSION_CODE = 123;
    public static final int UPLOAD_PERMISSION_CODE = 124;
    public static final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;
    private Bitmap bitmap;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ibProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvAddFriends);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCompletedPaths = view.findViewById(R.id.tvCompletedPaths);
        btnViewCompleted = view.findViewById(R.id.btnViewCompleted);

        btnLogOut = view.findViewById(R.id.btnLogOut);


       // requestStoragePermission();
        ibProfile = getView().findViewById(R.id.ivProfile);
        tvName = getView().findViewById(R.id.tvAddFriends);



        currentUser = ParseUser.getCurrentUser();

        ParseFile image = (ParseFile) currentUser.get("image");
        if (image != null){
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(400)).format(DecodeFormat.PREFER_ARGB_8888);
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(requestOptions).into(ibProfile);
        } else{
            ibProfile.setImageResource(R.drawable.ic_profile);
        }

        if (currentUser.get("firstName") != null){
            tvName.setText((String) currentUser.get("firstName"));
        }else {
            tvName.setText("user");
        }
        tvUserName.setText("@"+currentUser.getUsername());
        tvUserEmail.setText(currentUser.getEmail());

        // query through relation of completed paths for a user and display how many they have completed as size of returned objects
        ParseRelation<Path> completedPaths = ParseUser.getCurrentUser().getRelation("completedPaths");
        completedPaths.getQuery().findInBackground(new FindCallback<Path>() {
            @Override
            public void done(List<Path> objects, ParseException e) {
                if (e != null){
                    e.printStackTrace();
                } else {
                    if (objects.size() == 1){
                        tvCompletedPaths.setText("You have completed " + objects.size() + " path!");
                    } else{
                        tvCompletedPaths.setText("You have completed " + objects.size() + " paths!");
                    }

                }
            }
        });

        // set profile picture on click as a way to access gallery on phone to upload and change profile picture
        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestStoragePermission();
                showFileChooser();
            }
        });

        // user clicks on profile fragment 'View Completed Paths' to take to a new fragment where RecyclerView populated by only completed paths by the user
        btnViewCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment userPaths = new UserCompletedPathsFragment();
                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, userPaths).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Profile").commit();
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Log.d("logout", "logout");
                Intent logout = new Intent(getActivity(), MainActivity.class);
                startActivity(logout);
                Toast.makeText(getContext(), "Logout",Toast.LENGTH_SHORT).show();
            }
        });
    }




    // METHODS: accessing gallery (includes requesting reading and writing external storage permissions)
    private void requestStoragePermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return;
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }
    private void requestUploadPermission(){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
            return;
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, UPLOAD_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }

        if (requestCode == UPLOAD_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_LONG).show();
            }
        }
    }



    // on result of selecting an image from the gallery:
    // if request code valid: create a new bitmap from data stored from media, compress to JPEG, create a new ParseFile to update backend value for user
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data.getData() != null){
            filePath = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500,false);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] myDAT = baos.toByteArray();
                Random random = new Random();

                //create new ParseFile: random naming and save in background
                final ParseFile file = new ParseFile(random.nextInt(10000) + ".jpg", myDAT);
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e("EYEEE", "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                        Log.e("EYEEE", "Success");

                    }
                });
                // save to specific user under 'Image' which is the profile picture loaded
                currentUser.put("image", file);
                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e("YEET", "Error while saving");
                            e.printStackTrace();
                            return;
                        }
                        Log.e("YEET", "Success");
                        ParseFile image = (ParseFile) currentUser.get("image");
                        if (image != null){
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(275)).format(DecodeFormat.PREFER_ARGB_8888);
                            Glide.with(getContext())
                                    .load(image.getUrl()).apply(requestOptions).into(ibProfile);
                        } else{
                            ibProfile.setImageResource(R.drawable.ic_profile);
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    private void showFileChooser(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select a Picture"), PICK_IMAGE_REQUEST);
    }
}