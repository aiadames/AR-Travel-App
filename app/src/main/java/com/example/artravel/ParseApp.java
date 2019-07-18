package com.example.artravel;

import android.app.Application;

import com.example.artravel.models.Path;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Path.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("ar-travel")
                .clientKey("real-2019")
                .server("http://artravel.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
