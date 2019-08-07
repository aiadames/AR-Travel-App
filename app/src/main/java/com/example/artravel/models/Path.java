package com.example.artravel.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;


@ParseClassName("Path")
public class Path extends ParseObject {
    private static final String KEY_STOP_1 = "stop1";
    private static final String KEY_STOP_2 = "stop2";
    private static final String KEY_STOP_3 = "stop3";
    private static final String KEY_STOP_4 = "stop4";
    private static final String KEY_STOP_5 = "stop5";
    private static final String KEY_PATH_NAME = "pathName";
    private static final String KEY_PATH_DESCRIPTION = "pathDescription";
    private static final String KEY_PATH_AVG_RATING = "pathAvgRating";
    private static final String KEY_PATH_IMAGE = "pathImage";
    private static final String KEY_PATH_ALL_RATINGS = "pathRatings";
    private static final String KEY_PATH_GEM = "pathGem";
    private static final String KEY_PATH_THEME = "pathTheme";
    private boolean pathStarted = false;
    private boolean pathCompleted = false;
    private boolean pathBookmarked = false;

    ArrayList<Float> ratingList;


    public Path() {
    }

    public Stop getStop1() {
        return (Stop) getParseObject(KEY_STOP_1);
    }

    public Stop getStop2() {
        return (Stop) getParseObject(KEY_STOP_2);
    }

    public Stop getStop3() {
        return (Stop) getParseObject(KEY_STOP_3);
    }

    public Stop getStop4() {
        return (Stop) getParseObject(KEY_STOP_4);
    }

    public Stop getStop5() {
        return (Stop) getParseObject(KEY_STOP_5);
    }

    public String getPathName() {
        return getString(KEY_PATH_NAME);
    }

    public String getPathDescription() {
        return getString(KEY_PATH_DESCRIPTION);
    }

    public ParseFile getPathImage() {
        return getParseFile(KEY_PATH_IMAGE);
    }

    public ArrayList<Double> getPathRatings() {
        return (ArrayList<Double>) get(KEY_PATH_ALL_RATINGS);
    }

    public Gems getPathGem() {
        return (Gems)getParseObject(KEY_PATH_GEM);
    }

    public boolean getStartedPath(){
        return pathStarted;
    }

    public void setStartedPath(){
        pathStarted = true;
    }

    public boolean getCompletedPath(){
        return pathCompleted;
    }

    public void setCompletedPath(){
        pathCompleted = true;
    }

    public void setPathBookmarked(){
        pathBookmarked = true;
    }

    public void setPathUnbookmarked(){
        pathBookmarked = false;
    }

    public boolean getPathBookmarked(){
        return pathBookmarked;
    }


    public void setPathName(String pathName) {
        put(KEY_PATH_NAME, pathName);
    }

    public void setPathDescription(String pathDescription) {
        put(KEY_PATH_DESCRIPTION, pathDescription);
    }


    public void setPathAvgRating(){
        put(KEY_PATH_AVG_RATING, getPathRatingAvg());
    }

    public Float getPathAvgRating(){
        return (Float) get(KEY_PATH_AVG_RATING);
    }

    public void setStop1(Stop stop1) {
        put(KEY_STOP_1, stop1);
    }

    public void setStop2(Stop stop2) {
        put(KEY_STOP_2, stop2);
    }

    public void setStop3(Stop stop3) {
        put(KEY_STOP_3, stop3);
    }

    public void setStop4(Stop stop4) {
        put(KEY_STOP_4, stop4);
    }

    public void setStop5(Stop stop5) {
        put(KEY_STOP_5, stop5);
    }

    public ArrayList<CharSequence> getPathTheme(){
       return (ArrayList<CharSequence>)get(KEY_PATH_THEME);
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



    // HELPER METHODS: PATH RATINGS


    // for each path, when completed, allow a user to input a rating via RatingBar widget and update to Parse in Array of 'PathRatings'
    public void setPathRating(Float newRating) {
        ArrayList<Double> myAddedRating = getPathRatings();
        myAddedRating.add((double)newRating);
        put(KEY_PATH_ALL_RATINGS, myAddedRating);

    }


    // for each path, load the rating by querying for each Path's Array of 'PathRatings' and averaging individually
    public Float getPathRatingAvg(){
        ArrayList<Double> myRatingsList = getPathRatings();
        double sum = 0.0;
        for (int i = 0; i < getPathRatings().size(); i++){
            Object num = myRatingsList.get(i);
            double myFloat;
            if (num.getClass().equals(Integer.class)){
                myFloat = (double)((Integer)num);
            } else {
                myFloat = (double)(num);
            }
            sum = (sum + myFloat);
        }
        return (float)(sum/myRatingsList.size());
    }





}



