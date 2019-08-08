package com.example.artravel.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.artravel.R;
import com.example.artravel.models.Gems;
import com.example.artravel.models.Stop;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class EarnedGemDialogFragment extends DialogFragment {

    private TextView tvGemEarned;
    private ImageView ivEarnedGem;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.earned_gem_dialog, null);
        builder.setView(view);

        Bundle bundle = this.getArguments();
        Stop stop = Parcels.unwrap(bundle.getParcelable("Stop"));
        Gems gem = stop.getGem();

        tvGemEarned = view.findViewById(R.id.tvGemEarned);
        ivEarnedGem = view.findViewById(R.id.ivEarnedGem);
        tvGemEarned.setText("You earned the " + gem.getName() + " gem from the " + stop.getStopName() + " stop!");

        ParseFile image = gem.getImage();
        if (image != null) {
            Glide.with(getContext())
                    .load(image.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivEarnedGem);
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }
}
