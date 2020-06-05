package com.segu.url;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

import okhttp3.OkHttpClient;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}
