package com.example.artravel;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("ar-travel")
                .clientKey("real-2019")
                .server("http://artravel.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
