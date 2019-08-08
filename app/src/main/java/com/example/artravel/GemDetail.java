package com.example.artravel;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artravel.R;
import com.example.artravel.databinding.ActivityGemDetailBinding;
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
import com.google.ar.sceneform.math.QuaternionEvaluator;
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
    private SceneView sceneView;
    Scene scene;
    private String modelLink;
    private TransformationSystem transformationSystem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        currentGem = Parcels.unwrap(bundle.getParcelable("Gems"));

        ActivityGemDetailBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_gem_detail);

        DetailViewModel detailViewModel = new DetailViewModel(currentGem);
        binding.setDetailViewModel(detailViewModel);

        transformationSystem = new TransformationSystem(getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());

        sceneView = findViewById(R.id.detail_scene_view);

        renderObject();

//TODO
        // need to go back and sed if statements to appropriately size models and position camera
        //make models rotate
//        Camera camera = sceneView.getScene().getCamera();
//         camera.setLocalRotation(Quaternion.axisAngle(Vector3.right(), -30.0f));

        sceneView.getScene().addOnPeekTouchListener(new Scene.OnPeekTouchListener() {

            @Override
            public void onPeekTouch(HitTestResult hitResult, MotionEvent motionEvent) {
                try {
                    transformationSystem.onTouch(hitResult, motionEvent);
                    Log.e("Model", "Moving model");
                    //Toast.makeText(getApplicationContext(), "success moving model", Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Log.e("Model", "Cannot move model");
                    //Toast.makeText(getApplicationContext(), "CANT MOVE", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void renderObject() {



    modelLink = currentGem.getModel();
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(
                        this,
                        Uri.parse(modelLink),
                        RenderableSource.SourceType.GLTF2).build())
                .build().thenAccept(renderable -> {
                    scene = sceneView.getScene();
                    TransformableNode node = new TransformableNode(transformationSystem);

                    node.setRenderable(renderable);
                    node.getRotationController().setEnabled(true);
                    node.getScaleController().setEnabled(true);

                    node.getTranslationController().setEnabled(false);
                    node.setLocalScale(new Vector3(3f,3f,3f));
                    node.setLocalPosition(new Vector3(0f, -1f, -2f));

                    //node.setParent(scene);
                   sceneView.getScene().onAddChild(node);
                   transformationSystem.selectNode(node);
            Toast.makeText(getApplicationContext(), "successfully built model", Toast.LENGTH_SHORT).show();
            ObjectAnimator orbitAnimation = createAnimator();
            orbitAnimation.setTarget(node);
            // Set the speed of rotation
            orbitAnimation.setDuration(3500L);
            orbitAnimation.start();


        })
                       // tigerRenderable -> onRenderableLoaded(tigerRenderable)
                .exceptionally(
                        throwable -> {
                            Log.e("Model", "model failed to build");
                        //Toast.makeText(getApplicationContext(), "cant load model", Toast.LENGTH_SHORT).show();
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

    private static ObjectAnimator createAnimator() {
        // Node's setLocalRotation method accepts Quaternions as parameters.
        // First, set up orientations that will animate a circle.
        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 0);
        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 120);
        Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 240);
        Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 360);

        ObjectAnimator orbitAnimation = new ObjectAnimator();
        orbitAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);

        // Next, give it the localRotation property.
        orbitAnimation.setPropertyName("localRotation");

        // Use Sceneform's QuaternionEvaluator.
        orbitAnimation.setEvaluator(new QuaternionEvaluator());

        //  Allow orbitAnimation to repeat forever
        orbitAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        orbitAnimation.setRepeatMode(ObjectAnimator.RESTART);
        orbitAnimation.setInterpolator(new LinearInterpolator());
        orbitAnimation.setAutoCancel(true);

        return orbitAnimation;
    }
}

