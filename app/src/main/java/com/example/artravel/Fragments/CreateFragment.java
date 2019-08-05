package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.artravel.R;
import com.example.artravel.models.Path;
import com.parse.ParseException;
import com.parse.SaveCallback;

import org.parceler.Parcels;

public class CreateFragment extends Fragment {

    private EditText etPathName;
    private EditText etPathDescription;
    private ImageView ivPathImage;
    private Button btnContinuePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Create");

        etPathName = view.findViewById(R.id.etPathName);
        etPathDescription = view.findViewById(R.id.etPathDescription);
        ivPathImage = view.findViewById(R.id.ivPathImage);
        btnContinuePath = view.findViewById(R.id.btnPathContinue);

        btnContinuePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String pathName = etPathName.getText().toString();
                final String pathDescription = etPathDescription.getText().toString();

                // Create new path object
                Path newPath = new Path();
                newPath.setPathName(pathName);
                newPath.setPathDescription(pathDescription);

                // Save path on Parse server
                newPath.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                        } else {
                            Toast.makeText(getContext(), "Successfully created path!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Create new fragment to create the stops, pass newly made path in a bundle
                Fragment createStopFragment = new CreateStopFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("New Path", Parcels.wrap(newPath));
                createStopFragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity) getActivity()).getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContainer, createStopFragment).addToBackStack("Create")
                        .commit();
            }
        });
    }
}
