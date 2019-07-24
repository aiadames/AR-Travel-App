package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;

import org.parceler.Parcels;

public class QuestionFragment extends Fragment {


    private TextView stopQuestion;
    private TextView tvChoice1;
    private TextView tvChoice2;
    private TextView tvChoice3;
    private TextView tvChoice4;
    private Button btnChoice1;
    private Button btnChoice2;
    private Button btnChoice3;
    private Button btnChoice4;
    private Stop stop;
    private Path path;
    private String stopAnswer;
    String myCorrectButton;

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


        initializeBundleArguments();
        initializeViews();

        stopAnswer = stop.getStopAnswer();


        btnChoice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvChoice1.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnChoice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvChoice2.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnChoice3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvChoice3.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnChoice4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvChoice4.getText().toString().equalsIgnoreCase(stopAnswer)) {
                    Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void initializeViews() {
        stopQuestion.setText(stop.getStopQuestion());
        tvChoice1.setText(stop.getStopMultipleChoice().get(0));
        tvChoice2.setText(stop.getStopMultipleChoice().get(1));
        tvChoice3.setText(stop.getStopMultipleChoice().get(2));
        tvChoice4.setText(stop.getStopMultipleChoice().get(3));



    }

    private void initializeBundleArguments() {
        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
    }


}
