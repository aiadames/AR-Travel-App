package com.example.artravel.Fragments;

import android.util.Log;

import com.example.artravel.Activities.ARImageActivity;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.parse.ParseException;

import java.io.IOException;

public class ARImageFragment extends ArFragment {
    boolean enableAutoFocus = true;

    @Override
    protected Config getSessionConfiguration(Session session) {
        getPlaneDiscoveryController().setInstructionView(null);
        Config config = new Config(session);
        if (enableAutoFocus) {
            config.setFocusMode(Config.FocusMode.AUTO);
        } else {
            config.setFocusMode(Config.FocusMode.FIXED);
        }
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
        getArSceneView().setupSession(session);

        try {
            if ((((ARImageActivity) getActivity()).setupAugmentedImagesDb(config, session))) {
                Log.d("SetupAugImgDb", "Success");
            } else {
                Log.e("SetupAugImgDb","Faliure setting up db");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return config;
    }

}
