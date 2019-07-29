package com.example.artravel.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Manifest;
import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.facebook.login.Login;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
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



    public static final int STORAGE_PERMISSION_CODE = 123;
    public static final int UPLOAD_PERMISSION_CODE = 124;
    public static final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;
    private Bitmap bitmap;
    private Uri myImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestStoragePermission();
        ibProfile = view.findViewById(R.id.ivProfile);
        tvName = view.findViewById(R.id.tvName);
        tvWelcome = view.findViewById(R.id.tvWelcome);

        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvCompletedPaths = view.findViewById(R.id.tvCompletedPaths);
        btnViewCompleted = view.findViewById(R.id.btnViewCompleted);




        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(300
        )).format(DecodeFormat.PREFER_ARGB_8888);

        currentUser = ParseUser.getCurrentUser();
        currentUser.getUsername();
        ParseFile image = (ParseFile) currentUser.get("image");
        if (image != null){
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(requestOptions).into(ibProfile);
        } else{
            ibProfile.setImageResource(R.drawable.ic_profile);
        }

        String yer = (String) currentUser.get("firstName");
        tvName.setText(yer);

        tvUserName.setText("@"+currentUser.getUsername());
        tvUserEmail.setText(currentUser.getEmail());

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



        ibProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        btnViewCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "viewing completed paths", Toast.LENGTH_LONG).show();
                Fragment userPaths = new UserCompletedPathsFragment();
                FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, userPaths).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Path Detail").commit();
            }
        });


    }


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
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_LONG);
            } else{
                Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_LONG);
            }
        }

        if (requestCode == UPLOAD_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(), "Permission granted", Toast.LENGTH_LONG);
            } else{
                Toast.makeText(getContext(), "Permission not granted", Toast.LENGTH_LONG);
            }
        }
    }




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

                //create parse File
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


             //   Glide.with(getContext()).load(file.getUrl()).into(ibProfile);

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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        requestUploadPermission();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }








}
