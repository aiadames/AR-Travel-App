package com.example.artravel.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;


@ParseClassName("Gems")
public class Gems extends ParseObject {
    private static final String KEY_GEM_NAME = "gemName";
    private static final String KEY_GEM_DESCRIPTION = "gemDescription";
    private static final String KEY_GEM_IMAGE = "gemImage";
    private static final String KEY_GEM_LOCATION_CLUE = "gemLocationClue";
    private static final String KEY_GEM_LOCATION_IMAGE = "gemLocationImage";
    private static final String KEY_GEM_LOCATION = "gemLocation";
    private static final String KEY_GEM_MODEL = "Model";

    public String getDescription() {
        return getString(KEY_GEM_DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_GEM_IMAGE);
    }

    public String getName() {
        return getString(KEY_GEM_NAME);
    }

    public String getGemLocationClue() {
        return getString(KEY_GEM_LOCATION_CLUE);
    }

    public ParseFile getGemLocationImage() {
        return getParseFile(KEY_GEM_LOCATION_IMAGE);
    }

    public ParseGeoPoint getGemLocation() {
        return getParseGeoPoint(KEY_GEM_LOCATION);
    }

    public String getModel(){return getString(KEY_GEM_MODEL); }
}

