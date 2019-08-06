package com.example.artravel.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.artravel.Fragments.ARImageFragment;
import com.example.artravel.Fragments.CompletedPathFragment;
import com.example.artravel.Fragments.DetailedPathFragment;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class ARImageActivity extends AppCompatActivity implements View.OnClickListener {

    ArFragment arFragment;
    Gems gem;
    boolean shouldAddModel = true;

    Stop stop;
    Path path;
    ArrayList<Stop> stopsList;
    int stopIndex;

    private TextView stopQuestion;
    private TextView tvChoice1;
    private TextView tvChoice2;
    private TextView tvChoice3;
    private TextView tvChoice4;
    private TextView tvUserAttemptsLeft;
    private Button btnChoice1;
    private Button btnChoice2;
    private Button btnChoice3;
    private Button btnChoice4;
    private String stopAnswer;
    private FloatingActionButton fabReturn;

    private ConstraintLayout bottomSheet;

    private Integer userAttemptsLeft;
    private boolean recievesGem;
    private boolean answeredQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arimage);

        Intent intent = getIntent();
        gem = Parcels.unwrap(intent.getParcelableExtra("Gem"));
        stop = Parcels.unwrap(intent.getParcelableExtra("Stop"));
        path = Parcels.unwrap(intent.getParcelableExtra("Path"));
        stopsList = Parcels.unwrap(intent.getParcelableExtra("Stops Array"));
        stopIndex = intent.getIntExtra("Stop Index", 0);

        arFragment = (ARImageFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);


        stopQuestion = findViewById(R.id.tvStopQuestion);
        btnChoice1 = findViewById(R.id.btnChoice1);
        btnChoice2 = findViewById(R.id.btnChoice2);
        btnChoice3 = findViewById(R.id.btnChoice3);
        btnChoice4 = findViewById(R.id.btnChoice4);
        tvChoice1 = findViewById(R.id.tvChoice1);
        tvChoice2 = findViewById(R.id.tvChoice2);
        tvChoice3 = findViewById(R.id.tvChoice3);
        tvChoice4 = findViewById(R.id.tvChoice4);
        tvUserAttemptsLeft = findViewById(R.id.tvAttemptsLeft);
        bottomSheet = findViewById(R.id.question_bottom_sheet);
        fabReturn = findViewById(R.id.fabReturn);

        // setting up OnClickListener override method for switch statement
        btnChoice1.setOnClickListener(this);
        btnChoice2.setOnClickListener(this);
        btnChoice3.setOnClickListener(this);
        btnChoice4.setOnClickListener(this);

        initializeValues();
        initializeViews();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setPeekHeight(0);

    }

    public boolean setupAugmentedImagesDb(Config config, Session session) throws ParseException {
        AugmentedImageDatabase augmentedImageDatabase;

        ParseFile gemLocationImage = gem.getGemLocationImage();
        byte[] data = gemLocationImage.getData();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.augmented_images_earth);
        if (bitmap == null) {
            return false;
        }
        augmentedImageDatabase = new AugmentedImageDatabase(session);
        augmentedImageDatabase.addImage("gem", bitmap);
        config.setAugmentedImageDatabase(augmentedImageDatabase);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : augmentedImages) {
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING) {
                if (augmentedImage.getName().equals("gem") && shouldAddModel) {
                    placeObject(arFragment, augmentedImage.createAnchor(augmentedImage.getCenterPose()), Uri.parse("Diamond.sfb"));
                    shouldAddModel = false;
                    BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                    bottomSheetBehavior.setPeekHeight(1000);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void placeObject(ArFragment arFragment, Anchor anchor, Uri uri) {
        ModelRenderable.builder()
                .setSource(arFragment.getContext(), uri)
                .setSource(arFragment.getContext(),
                        RenderableSource.builder().setSource(
                        arFragment.getContext(),
                        Uri.parse(gem.getModel()),
                        RenderableSource.SourceType.GLTF2).build())
                .build()
                .thenAccept(modelRenderable -> addNodeToScene(arFragment, anchor, modelRenderable))
                .exceptionally(throwable -> {
                            Toast.makeText(arFragment.getContext(), "Error:" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                            return null;
                        }
                );
    }

    private void addNodeToScene(ArFragment arFragment, Anchor anchor, Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());

        //set rotation in direction (x,y,z) in degrees 90
        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 0, 1f), 90f));

        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.select();

        // Scale size of the AR model
        node.getScaleController().setMaxScale(0.05f);
        node.getScaleController().setMinScale(0.01f);
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    // for each button click if the corresponding text answer matches the Stop object answer
    // if so, call helper method correctAnswer to display corresponding changes, else call falseAnswer
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChoice1:
                if (tvChoice1.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    correctAnswer(btnChoice1);
                } else {
                    falseAnswer();
                }
                break;
            case R.id.btnChoice2:
                if (tvChoice2.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    correctAnswer(btnChoice2);
                } else {
                    falseAnswer();
                }
                break;
            case R.id.btnChoice3:
                if (tvChoice3.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    correctAnswer(btnChoice3);
                } else {
                    falseAnswer();
                }
                break;
            case R.id.btnChoice4:
                if (tvChoice4.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    correctAnswer(btnChoice4);
                } else {
                    falseAnswer();
                }
                break;
        }

        // update the user on how many attempts they have via the text view display
        // additionally, check if has answered question or if user attempts are depleted
        updateTextView(userAttemptsLeft);
        if (userAttemptsLeft == 0 || answeredQuestion == true){
            doneAnswering();
        }
    }


    // set displays of all mutliple choice answers, attempts left, and question to answer
    private void initializeViews() {
        stopQuestion.setText(stop.getStopQuestion());
        tvUserAttemptsLeft.setText("Attempts Left: " + userAttemptsLeft.toString());
        tvChoice1.setText(stop.getStopMultipleChoice().get(0));
        tvChoice2.setText(stop.getStopMultipleChoice().get(1));
        tvChoice3.setText(stop.getStopMultipleChoice().get(2));
        tvChoice4.setText(stop.getStopMultipleChoice().get(3));
        fabReturn.hide();
    }

    // unwrap bundle to get Stop and Path object for data user, also set up all  initial
    // values for this run of the fragment such as the user attempts, if question answered, and if gets gem
    private void initializeValues() {
        // Get bundle with stops, path, and current stop
//        Bundle bundle = this.getArguments();
//        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
//        path = Parcels.unwrap(bundle.getParcelable("Path"));
//        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
//        stopIndex = bundle.getInt("Stop Index");

        userAttemptsLeft = 3;
        recievesGem = false;
        answeredQuestion = false;
        stopAnswer = stop.getStopAnswer();
    }

    private void doneAnswering(){
        if(answeredQuestion && userAttemptsLeft >=0){
            // launch camera if we implement AR recognition will go here
            Toast.makeText(this, "congrats, you get a gem!", Toast.LENGTH_SHORT).show();
            // add gems to relation of specific user for passport use
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<Gems> relation = user.getRelation("collectedGems");
            relation.add(stop.getGem());
            user.saveInBackground();
        } else{
            Toast.makeText(this, "sorry, you don't get a gem!", Toast.LENGTH_SHORT).show();
        }

        // reset values for next time fragment is launched? (need to map out lifecycle of this fragment)
        resetValues();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseRelation<Stop> stopRelation = currentUser.getRelation("visitedStops");
            stopRelation.add(stop);
            currentUser.saveInBackground();
        }

        fabReturn.show();
        fabReturn.setClickable(true);
        fabReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ARImageActivity.this, HomeActivity.class);
                intent.putExtra("Fragment", "Open Stop Fragment");
                intent.putExtra("Path", Parcels.wrap(path));
                intent.putExtra("Stops Array", Parcels.wrap(stopsList));
                intent.putExtra("Stop Index", stopIndex);
                intent.putExtra("Stop", Parcels.wrap(stop));
                startActivity(intent);
            }
        });

//        // send intent
//        Fragment detailedPathFragment = new DetailedPathFragment();
//        Fragment doneFragment = new CompletedPathFragment();
//
//
//
//        // Pass bundle with path
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("Path", Parcels.wrap(path));
//        bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
//        if (stopIndex < stopsList.size() - 1) {
//            //stopIndex++;
//            stopsList.remove(stop);
//            // add gems to relation of specific user for passport use
//            detailedPathFragment.setArguments(bundle);
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.flContainer, detailedPathFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop")
//                    .commit();
//        } else if (stopIndex == stopsList.size() - 1){
//            // query for started paths relation and remove this path
//            // query for completed paths relation and add this path
//            ParseUser user = ParseUser.getCurrentUser();
//            ParseRelation<Path> startedPaths = user.getRelation("startedPaths");
//            startedPaths.remove(path);
//            user.saveInBackground();
//            ParseRelation<Path> completedPaths = user.getRelation("completedPaths");
//            completedPaths.add(path);
//            user.saveInBackground();
//            doneFragment.setArguments(bundle);
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.flContainer, doneFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop")
//                    .commit();
//        }
    }

    public void updateTextView(Integer userAttemptsLeft) {
        tvUserAttemptsLeft.setText("Attempts Left: " + userAttemptsLeft.toString());
    }

    public void resetValues(){
        answeredQuestion = false;
        userAttemptsLeft = 3;
        recievesGem = false;
    }

    // change value of answered question to true and change button background color to green
    public void correctAnswer(Button button){
        Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        button.setBackgroundResource(R.color.green);
        answeredQuestion = true;
    }
    // change value of attempts by 1
    public void falseAnswer(){
        Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        userAttemptsLeft -= 1;
    }

}
