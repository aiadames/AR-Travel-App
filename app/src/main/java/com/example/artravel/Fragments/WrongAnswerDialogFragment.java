package com.example.artravel.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;

import org.parceler.Parcels;

import java.util.ArrayList;

public class WrongAnswerDialogFragment extends DialogFragment {

    Stop stop;
    Path path;
    ArrayList<Stop> stopsList;
    int stopIndex;

    private TextView tvCorrectAnswer;
    private Button btnWrongAnswerContinue;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.wrong_answer_dialog, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");

        tvCorrectAnswer = view.findViewById(R.id.tvCorrectAnswer);
        btnWrongAnswerContinue = view.findViewById(R.id.btnWrongAnswerContinue);

        tvCorrectAnswer.setText("The correct answer is " + stop.getStopAnswer() + ".");

        btnWrongAnswerContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("Fragment", "Open Stop Fragment");
                intent.putExtra("Path", Parcels.wrap(path));
                intent.putExtra("Stops Array", Parcels.wrap(stopsList));
                intent.putExtra("Stop Index", stopIndex);
                intent.putExtra("Stop", Parcels.wrap(stop));
                startActivity(intent);
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
