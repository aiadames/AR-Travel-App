package com.example.artravel.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.example.artravel.Fragments.ShareFragment;
import com.example.artravel.GemsAdapter;
import com.example.artravel.arGemsAdapter;
import com.example.artravel.models.Gems;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.Parcel;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.artravel.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.ssaurel.screenshot.Screenshot;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_sceneform);

        setupView();
        queryGems();

        int selected;
        selected = adapter.getSelected();
        Toast.makeText(this, "selected is equals to " + selected, Toast.LENGTH_SHORT).show();

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "clicked share", Toast.LENGTH_SHORT).show();
                //bitmap = Screenshot.getScreenShot(fragment);

                Frame currentFrame = fragment.getArSceneView().getArFrame();
                try{
                     image = currentFrame.acquireCameraImage();

                } catch(Exception e){

            }

                bitmap = Screenshot.getScreenShot(image);
                showEditDialog();
            }
        });

        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            int selection;
            selection = adapter.getSelected();
            Toast.makeText(getApplicationContext(), "selected == " + selection, Toast.LENGTH_SHORT).show();

            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(fragment.getArSceneView().getScene());
            loadModel();
            setModel(anchorNode);


            Toast.makeText(this, "selected is equals to " + selection, Toast.LENGTH_SHORT).show();
        });

    }

    private void loadModel() {
        modelLink = adapter.getImageLink();
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

    private void showEditDialog() {

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bundle bundle = new Bundle();
        bundle.putByteArray("image",byteArray);



        FragmentManager fm = getSupportFragmentManager();
        ShareFragment shareFragment = ShareFragment.newInstance("Some Title");
        shareFragment.setArguments(bundle);
        shareFragment.show(fm, "share_confirmation");
    }


}