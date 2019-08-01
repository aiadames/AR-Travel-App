package com.example.artravel.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;


@ParseClassName("Stop")

public class Stop extends ParseObject {
    private static final String KEY_STOP_LOCATION = "stopLocation";
    private static final String KEY_STOP_NAME = "stopName";
    private static final String KEY_STOP_DETAILS = "stopDetails";
    private static final String KEY_GEM= "stopGem";
    private static final String KEY_STOP_IMAGE = "stopImage";
    private static final String KEY_INFO_PARAGRAPH = "infoParagraph";
    private static final String KEY_MULTIPLE_CHOICE = "stopMultipleChoice";
    private static final String KEY_STOP_ANSWER = "stopAnswer";
    private static final String KEY_STOP_QUESTION = "stopQuestion";
    private static final String KEY_STOP_DETAILS_IMAGE = "stopDetailsImage";



    public Stop() {
    }

    public ParseGeoPoint getStopLocation() {
        return getParseGeoPoint(KEY_STOP_LOCATION);
    }

    public String getStopName() {
        return getString(KEY_STOP_NAME);
    }

    public String getStopDetails() {
        return getString(KEY_STOP_DETAILS);
    }

    public Gems getGem() {
        return (Gems) getParseObject(KEY_GEM);
    }

    public ParseFile getStopImage() {
        return getParseFile(KEY_STOP_IMAGE);
    }

    public String getInfoParagraph() {
        return getString(KEY_INFO_PARAGRAPH);
    }

    public ArrayList<String> getStopMultipleChoice(){
        return (ArrayList<String>)get(KEY_MULTIPLE_CHOICE);
    }
    public String getStopAnswer(){
        return getString(KEY_STOP_ANSWER);
    }


    public String getStopQuestion(){
        return getString(KEY_STOP_QUESTION);
    }

    public ParseFile getStopDetailsImage() {
        return getParseFile(KEY_STOP_DETAILS_IMAGE);
    }

    public void setStopName(String stopName) {
        put(KEY_STOP_NAME, stopName);
    }

    public void setStopDescription(String stopDescription) {
        put(KEY_STOP_DETAILS, stopDescription);
    }

    public void setStopQuestion(String stopQuestion) {
        put(KEY_STOP_QUESTION, stopQuestion);
    }

    public void setStopMultipleChoice(ArrayList<String> multipleChoice) {
        put(KEY_MULTIPLE_CHOICE, multipleChoice);
    }

    public void setStopInfoParagraph(String infoParagraph) {
        put(KEY_INFO_PARAGRAPH, infoParagraph);
    }

    public void setStopAnswer(String answer) {
        put(KEY_STOP_ANSWER, answer);
    }

}
