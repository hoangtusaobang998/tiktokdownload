package com.sanfulou.tiktokdownload;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
        setupNotificationChannel();
        MobileAds.initialize(this, initializationStatus -> {
        });
    }

    private void setupNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(this.getPackageName(), "DownloadFile",
                    NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(manager).createNotificationChannel(channel);
        }

    }
}
