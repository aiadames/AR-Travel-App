package com.example.artravel.Fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.google.ar.core.Anchor;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseTransformableNode;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import org.parceler.Parcels;

public class GemDetail extends AppCompatActivity {

    private Gems currentGem;
    private TextView gemName;
    private TextView gemDescription;
    private ImageView gemImage;
    private ArFragment fragment;
    private SceneView sceneView;
    private Node node;
    private TransformableNode tigerNode;
    Scene scene;
    private String modelLink;


    private ModelRenderable tigerRenderable;
    private TransformationSystem transformationSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gem_detail);

        setupView();

        Bundle bundle = getIntent().getExtras();
        currentGem = Parcels.unwrap(bundle.getParcelable("Gems"));

        setValues(currentGem);
        renderOcject();

        transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());
        //transformationSystem.selectNode((BaseTransformableNode) tigerNode);

        sceneView.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {

            @Override
            public void onPeekTouch(HitTestResult hitResult, MotionEvent motionEvent) {
                try {
                    transformationSystem.onTouch(hitResult, motionEvent);
                } catch (Exception exception) {
                    Toast.makeText(getApplicationContext(), "CANT MOVE", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void renderOcject() {

        scene = new Scene(sceneView);

    modelLink = currentGem.getModel();
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        Uri.parse(modelLink),
                        RenderableSource.SourceType.GLTF2).build())
                .build().thenAccept(tigerRenderable -> onRenderableLoaded(tigerRenderable))
                .exceptionally(
                        throwable -> {
                            Log.e("Model", "model failed to build");
                        Toast.makeText(getApplicationContext(), "cant load model", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            sceneView.resume();
        }
        catch (CameraNotAvailableException e){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        sceneView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sceneView.destroy();
    }

    private void onRenderableLoaded(Renderable renderable) {
        tigerNode = new TransformableNode(transformationSystem);
        tigerNode.setRenderable(renderable);
        tigerNode.setParent(scene);
        tigerNode.setLocalScale(new Vector3(5f,5f,5f));
        tigerNode.setLocalPosition(new Vector3(0f, -1f, -2f));
        tigerNode.addChild(tigerNode);

        tigerNode.select();
        transformationSystem.selectNode(tigerNode);
      //  Camera camera = sceneView.getScene().getCamera();
     //   camera.setLocalRotation(Quaternion.axisAngle(Vector3.right(), -30.0f));
    }

    private void setValues(Gems currentGem) {
       gemName.setText(currentGem.getName());
       gemDescription.setText(currentGem.getDescription());
    }

    private void setupView() {
        sceneView = findViewById(R.id.detail_scene_view);
        gemName = findViewById(R.id.tvdetailName);
        gemDescription = findViewById(R.id.tvdetailDescr);


    }


}