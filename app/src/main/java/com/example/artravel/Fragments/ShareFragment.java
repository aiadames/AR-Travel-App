package com.example.artravel.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.artravel.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class ShareFragment extends DialogFragment {
    private ImageView imageView;
    private Button share;
    private Button cancel;
    private String path;
    private Bitmap bitmap;
    private String filename;

    public ShareFragment() {
    }

    public static ShareFragment newInstance(String title) {
        ShareFragment frag = new ShareFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupView(view);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_image(filename);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
    }

    private void setupView(View view) {
        imageView = view.findViewById(R.id.imageView3);
        share = view.findViewById(R.id.btnShareConfirm);
        cancel = view.findViewById(R.id.btnCancel);

        Bundle bundle;
        bundle = this.getArguments();
        filename = bundle.getString("filename");
        bitmap = BitmapFactory.decodeFile(filename);
        imageView.setImageBitmap(bitmap);

    }


    private void share_image(String filename) {

        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(),
                bitmap, "Image", null);

        Uri uri = Uri.parse(path);

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, "Checkout Hidden Gems");
        getContext().startActivity(Intent.createChooser(share, "Share Your Gems!"));
    }

}
