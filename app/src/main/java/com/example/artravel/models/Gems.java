package com.example.artravel.models;


import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Gems")
public class Gems extends ParseObject {
    private static final String KEY_GEM_NAME = "gemName";
    private static final String KEY_GEM_DESCRIPTION = "gemDescription";
    private static final String KEY_GEM_IMAGE = "gemImage";


    public String getDescription() {
        return getString(KEY_GEM_DESCRIPTION);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_GEM_IMAGE);
    }

    public String getName() {
        return getString(KEY_GEM_NAME);
    }

}

