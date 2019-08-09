package com.example.artravel.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.example.artravel.Fragments.ShareFragment;
import com.example.artravel.arGemsAdapter;
import com.example.artravel.models.Gems;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.widget.Button;
import android.widget.Toast;

import com.example.artravel.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ARGemViewer extends AppCompatActivity {

    private ArrayList mGems;
    private RecyclerView rvGems;
    private arGemsAdapter adapter;
    private String modelLink;
    ArFragment fragment;
    private Button btnShare;
    private String sharePath;
    private Bitmap  bitmap;
    private ModelRenderable polyRenderable;
    private Image image;
    private int selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_sceneform);

        setupView();
        queryGems();

     selected = adapter.getSelected();
//        Toast.makeText(this, "selected is equals to " + selected, Toast.LENGTH_SHORT).show();

        btnShare.setOnClickListener(view -> takePhoto()) ;
        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> fragmentTap(hitResult, plane, motionEvent));

    }

    private void fragmentTap(HitResult hitResult, Plane plane, MotionEvent motionEvent)
        {
            selected = adapter.getSelected();
            Toast.makeText(getApplicationContext(), "selected == " + selected, Toast.LENGTH_SHORT).show();

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(fragment.getArSceneView().getScene());
            loadModel();
            setModel(anchorNode);
            Toast.makeText(this, "selected is equals to " + selected, Toast.LENGTH_SHORT).show();


    }

    private void loadModel() {
        modelLink = adapter.getImageLink(selected);
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        Uri.parse(modelLink),
                        RenderableSource.SourceType.GLTF2).build())
                .build().thenAccept(modelRenderable -> polyRenderable = modelRenderable)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "cant load model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
    }


    private void setModel(AnchorNode anchorNode) {

        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(polyRenderable);
        node.select();

            // Scale size of the AR model
            node.getScaleController().setMaxScale(0.6f);
            node.getScaleController().setMinScale(0.5f);

    }

    private void setupView() {

        mGems = new ArrayList<>();
        rvGems = findViewById(R.id.rvSceneForm);
        btnShare = findViewById(R.id.btnShare);
        adapter = new arGemsAdapter(mGems, this.getApplicationContext());
        rvGems.setAdapter(adapter);
        rvGems.setLayoutManager(new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        fragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_passport_fragment);
    }

    private void queryGems() {


        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            Toast.makeText(this.getApplicationContext(), "user null", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this.getApplicationContext(), "user " + user.getUsername() + " is not null", Toast.LENGTH_SHORT).show();

        ParseRelation<Gems> relation;
        relation = user.getRelation("collectedGems");
        relation.getQuery().findInBackground(new FindCallback<Gems>() {
            @Override
            public void done(List<Gems> userGems, ParseException e) {
                if (e != null) {
                    Log.e("query", "error in query");
                    e.printStackTrace();
                    return;
                }
                mGems.addAll(userGems);

                // Toast.makeText(getContext(), numCollected + " gems collected", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();

            }
        });
    }

    private void showEditDialog(String filename) {


        Bundle bundle = new Bundle();
        bundle.putString("filename",filename);
        FragmentManager fm = getSupportFragmentManager();
        ShareFragment shareFragment = ShareFragment.newInstance("Share image");
        shareFragment.setArguments(bundle);
        shareFragment.show(fm, "share_confirmation");
    }

    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView view = fragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                    showEditDialog(filename);


                } catch (IOException e) {
                    Toast toast = Toast.makeText(ARGemViewer.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
    }

}