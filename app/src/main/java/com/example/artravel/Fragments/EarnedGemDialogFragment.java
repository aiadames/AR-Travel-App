package com.example.artravel.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.Activities.ARImageActivity;
import com.example.artravel.Activities.HomeActivity;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Path;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;

public class EarnedGemDialogFragment extends DialogFragment {

    private TextView tvGemEarned;
    private ImageView ivEarnedGem;
    private Button btnDialogContinue;

    Stop stop;
    Path path;
    ArrayList<Stop> stopsList;
    int stopIndex;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.earned_gem_dialog, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        path = Parcels.unwrap(bundle.getParcelable("Path"));
        stopsList = Parcels.unwrap(bundle.getParcelable("Stops Array"));
        stopIndex = bundle.getInt("Stop Index");
        Gems gem = stop.getGem();

        tvGemEarned = view.findViewById(R.id.tvGemEarned);
        ivEarnedGem = view.findViewById(R.id.ivEarnedGem);
        btnDialogContinue = view.findViewById(R.id.btnDialogContinue);
        tvGemEarned.setText("You earned the " + gem.getName() + " gem from the " + stop.getStopName() + " stop!");

        ParseFile image = gem.getImage();
        if (image != null) {
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivEarnedGem);
        }

        btnDialogContinue.setOnClickListener(new View.OnClickListener() {
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
