package com.example.artravel.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.artravel.R;

import static com.example.artravel.R.layout.fragment_home;
import static com.example.artravel.R.layout.fragment_passport;

public class PassportFragment extends Fragment {

  //  private Button btnTest;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return (View) inflater.inflate(fragment_passport,container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        btnTest = view.findViewById(R.id.btnTest);
//
//        btnTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast. makeText(getContext(), "Passport Test",Toast.LENGTH_SHORT).show();
//            }
//
//        });


    }
}
