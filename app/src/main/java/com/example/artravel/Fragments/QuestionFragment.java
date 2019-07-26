package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;


import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

public class QuestionFragment extends Fragment implements View.OnClickListener {


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
    private Stop stop;
    private Path path;
    private String stopAnswer;
    String myCorrectButton;

    private List<Stop> stopsList;
    private int stopIndex;


    private Integer userAttemptsLeft;
    private boolean recievesGem;
    private boolean answeredQuestion;
    private static final int NUM_STOPS = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stop_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        stopQuestion = view.findViewById(R.id.tvStopQuestion);
        btnChoice1 = view.findViewById(R.id.btnChoice1);
        btnChoice2 = view.findViewById(R.id.btnChoice2);
        btnChoice3 = view.findViewById(R.id.btnChoice3);
        btnChoice4 = view.findViewById(R.id.btnChoice4);
        tvChoice1 = view.findViewById(R.id.tvChoice1);
        tvChoice2 = view.findViewById(R.id.tvChoice2);
        tvChoice3 = view.findViewById(R.id.tvChoice3);
        tvChoice4 = view.findViewById(R.id.tvChoice4);
        tvUserAttemptsLeft = view.findViewById(R.id.tvAttemptsLeft);

        // setting up OnClickListener override method for switch statement
        btnChoice1.setOnClickListener(this);
        btnChoice2.setOnClickListener(this);
        btnChoice3.setOnClickListener(this);
        btnChoice4.setOnClickListener(this);

        initializeValues();
        initializeViews();

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



    }

    // unwrap bundle to get Stop and Path object for data user, also set up all  initial
    // values for this run of the fragment such as the user attempts, if question answered, and if gets gem
    private void initializeValues() {
        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");

        userAttemptsLeft = 3;
        recievesGem = false;
        answeredQuestion = false;
        stopAnswer = stop.getStopAnswer();
    }

    private void doneAnswering(){
        if(answeredQuestion && userAttemptsLeft >=0){
            // launch camera if we implement AR recognition will go here
            Toast.makeText(getContext(), "congrats, you get a gem!", Toast.LENGTH_SHORT).show();
            // add gems to relation of specific user for passport use
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<Gems> relation = user.getRelation("collectedGems");
            relation.add(stop.getGem());
            user.saveInBackground();
        } else{
            Toast.makeText(getContext(), "sorry, you don't get a gem!", Toast.LENGTH_SHORT).show();
        }

        // reset values for next time fragment is launched? (need to map out lifecycle of this fragment)
        resetValues();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            ParseRelation<Stop> stopRelation = currentUser.getRelation("visitedStops");
            stopRelation.add(stop);
            currentUser.saveInBackground();
        }


        // send intent to next stop
        Fragment stopFragment = new StopFragment();
        Fragment doneFragment = new CompletedPathFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("Path", Parcels.wrap(path));
        bundle.putParcelable("Stops Array", Parcels.wrap(stopsList));
        if (stopIndex < stopsList.size() - 1) {
            stopIndex++;
            // add gems to relation of specific user for passport use
            bundle.putInt("Stop Index", stopIndex);
            stopFragment.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContainer, stopFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop")
                    .commit();
        } else if (stopIndex == stopsList.size() - 1){
            // query for started paths relation and remove this path
            // query for completed paths relation and add this path
            ParseUser user = ParseUser.getCurrentUser();
            ParseRelation<Path> startedPaths = user.getRelation("startedPaths");
            startedPaths.remove(path);
            user.saveInBackground();
            ParseRelation<Path> completedPaths = user.getRelation("completedPaths");
            completedPaths.add(path);
            user.saveInBackground();
            doneFragment.setArguments(bundle);
            FragmentManager fragmentManager = ((AppCompatActivity)getActivity()).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContainer, doneFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack("Stop")
                    .commit();
        }

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
        Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
        button.setBackgroundResource(R.color.green);
        answeredQuestion = true;
    }
    // change value of attempts by 1
    public void falseAnswer(){
        Toast.makeText(getContext(), "Wrong!", Toast.LENGTH_SHORT).show();
        userAttemptsLeft -= 1;
    }


}
