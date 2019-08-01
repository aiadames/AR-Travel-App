package com.example.artravel.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;

public class TabStopFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private EditText etStopName;
    private EditText etStopDescription;
    private EditText etStopQuestion;
    private EditText etAnswer1;
    private EditText etAnswer2;
    private EditText etAnswer3;
    private EditText etAnswer4;
    private EditText etStopInfoParagraph;

    private RadioGroup rgAnswers;
    private int answerButtonId;

    private Button btnSaveStop;

    private Path newPath;

    public static TabStopFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        TabStopFragment fragment = new TabStopFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_stop, container, false);
        TextView textView = view.findViewById(R.id.tvFragmentNumber);
        textView.setText("Fragment #" + mPage);

        // Get bundle containing the newly made path object
        Bundle bundle = this.getArguments();
        newPath = Parcels.unwrap(bundle.getParcelable("New Path"));

        etStopName = view.findViewById(R.id.etStopName);
        etStopDescription = view.findViewById(R.id.etStopDescription);
        etStopQuestion = view.findViewById(R.id.etStopQuestion);
        etStopInfoParagraph = view.findViewById(R.id.etStopInfoParagraph);
        etAnswer1 = view.findViewById(R.id.etAnswer1);
        etAnswer2 = view.findViewById(R.id.etAnswer2);
        etAnswer3 = view.findViewById(R.id.etAnswer3);
        etAnswer4 = view.findViewById(R.id.etAnswer4);
        rgAnswers = view.findViewById(R.id.rgAnswers);

        rgAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerButtonId = rgAnswers.getCheckedRadioButtonId();
            }
        });

        btnSaveStop = view.findViewById(R.id.btnSaveStop);
        btnSaveStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String stopName = etStopName.getText().toString();
                final String stopDescription = etStopDescription.getText().toString();
                final String stopQuestion = etStopQuestion.getText().toString();
                final String stopInfoParagraph = etStopInfoParagraph.getText().toString();

                String stopAnswer = "";
                switch(answerButtonId) {
                    case R.id.radioButton1:
                        stopAnswer = etAnswer1.getText().toString();
                        break;
                    case R.id.radioButton2:
                        stopAnswer = etAnswer2.getText().toString();
                        break;
                    case R.id.radioButton3:
                        stopAnswer = etAnswer3.getText().toString();
                        break;
                    case R.id.radioButton4:
                        stopAnswer = etAnswer4.getText().toString();
                        break;
                }

                ArrayList<String> multipleChoice = new ArrayList<>();
                multipleChoice.add(etAnswer1.getText().toString());
                multipleChoice.add(etAnswer2.getText().toString());
                multipleChoice.add(etAnswer3.getText().toString());
                multipleChoice.add(etAnswer4.getText().toString());

                // Create a new Stop object
                Stop newStop = new Stop();
                newStop.setStopName(stopName);
                newStop.setStopDescription(stopDescription);
                newStop.setStopQuestion(stopQuestion);
                newStop.setStopInfoParagraph(stopInfoParagraph);
                newStop.setStopMultipleChoice(multipleChoice);
                newStop.setStopAnswer(stopAnswer);

                // Save stop object on Parse server
                newStop.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            Toast.makeText(getContext(), "Successfully created stop!", Toast.LENGTH_SHORT).show();

                            // Switch statement to determine which stop is currently being added to the path
                            switch (mPage) {
                                case 0:
                                    newPath.setStop1(newStop);
                                    break;
                                case 1:
                                    newPath.setStop2(newStop);
                                    break;
                                case 2:
                                    newPath.setStop3(newStop);
                                    break;
                                case 3:
                                    newPath.setStop4(newStop);
                                    break;
                                case 4:
                                    newPath.setStop5(newStop);
                                    break;
                            }
                            // Save updated path to Parse server
                            newPath.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                    } else {
                                        Toast.makeText(getContext(), "Successfully saved path!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        return view;
    }
}
