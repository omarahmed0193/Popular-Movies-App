package com.udacity.popularmovies;


import io.realm.Realm;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize realm
        Realm.init(this);
    }
}
