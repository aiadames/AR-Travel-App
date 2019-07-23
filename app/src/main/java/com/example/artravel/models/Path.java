package com.example.artravel.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;


@ParseClassName("Path")
public class Path extends ParseObject {
    private static final String KEY_STOP_1 = "stop1";
    private static final String KEY_STOP_2 = "stop2";
    private static final String KEY_STOP_3 = "stop3";
    private static final String KEY_STOP_4 = "stop4";
    private static final String KEY_STOP_5 = "stop5";
    private static final String KEY_PATH_NAME = "pathName";
    private static final String KEY_PATH_DESCRIPTION = "pathDescription";
    private static final String KEY_PATH_RATING = "pathRating";
    private static final String KEY_PATH_IMAGE = "pathImage";

    private String mImageUrl;
    private String mPathTitle;
    private String mPathDescription;

    public Path() {}

    public Path(String imageUrl, String pathTitle, String pathDescription){
        mImageUrl = imageUrl;
        mPathTitle = pathTitle;
        mPathDescription = pathDescription;
    }

    public Stop getStop1() {
        return (Stop)getParseObject(KEY_STOP_1);
    }

    public Stop getStop2() {
        return (Stop)getParseObject(KEY_STOP_2);
    }

    public Stop getStop3() {
        return (Stop)getParseObject(KEY_STOP_3);
    }

    public Stop getStop4() {
        return (Stop)getParseObject(KEY_STOP_4);
    }

    public Stop getStop5() {
        return (Stop)getParseObject(KEY_STOP_5);
    }

    public String getPathName() {
        return getString(KEY_PATH_NAME);
    }

    public Integer getPathRating(){
        return getInt(KEY_PATH_RATING);
    }

    public String getPathDescription() {
        return getString(KEY_PATH_DESCRIPTION);
    }

    public ParseFile getPathImage(){
        return getParseFile(KEY_PATH_IMAGE);
    }


    public static class Query extends ParseQuery<Path> {

        public Query() {
            super(Path.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }

        public Query withUser() {
            include("user");
            return this;
        }
    }
}
