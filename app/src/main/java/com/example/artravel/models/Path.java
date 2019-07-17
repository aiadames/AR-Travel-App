package com.example.artravel.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Path")
public class Path  {
    private static final String KEY_STOP_1 = "stop1";
    private static final String KEY_STOP_2 = "stop2";
    private static final String KEY_STOP_3 = "stop3";
    private static final String KEY_STOP_4 = "stop4";
    private static final String KEY_STOP_5 = "stop5";

    private String mImageUrl;
    private String mPathTitle;
    private String mPathDescription;

    public Path(String imageUrl, String pathTitle, String pathDescription){
        mImageUrl = imageUrl;
        mPathTitle = pathTitle;
        mPathDescription = pathDescription;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public String getPathTitle(){
        return mPathTitle;
    }

    public String getPathDescription(){
        return mPathDescription;
    }


}
