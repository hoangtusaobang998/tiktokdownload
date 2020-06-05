package com.sanfulou.tiktokdownload.views;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.service.VideoWallpaperService;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sanfulou.tiktokdownload.init.Init.sendMessenger;

public class DetailsVideo extends AppCompatActivity {

    public static final String URL_VIDEO = "URL_VIDEO";
    public static final String POSITION = "POSITION";
    private VideoView videoView;
    private MediaPlayer mediaPlayer = null;
    private SharedPreferences sharedPreferences;
    String urlvideo = null;
    private boolean is = false;
    AudioManager audioManager;
    private SeekBar vol;
    private Switch doubleClick;
    Float[] values = {0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f};
    private boolean isClick = false;
    private boolean isFeleteFile = false;

    @OnClick(R.id.view)
    public void close() {
        finish();
    }

    @OnClick(R.id.view_a)
    public void delete() {

        isFeleteFile = true;
        try {
            File file = new File(urlvideo);
            file.delete();
            setResult(RESULT_OK, getIntent());
            finish();

        } catch (Exception ignored) {

        }

    }

    @OnClick(R.id.view_b)
    public void share() {
        sendMessenger(new File(urlvideo), DetailsVideo.this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_video);
        ButterKnife.bind(this);
        sharedPreferences = getSharedPreferences("DATA", MODE_PRIVATE);
        urlvideo = getIntent().getStringExtra(URL_VIDEO);
        if (urlvideo == null) {
            finish();
        }
        videoView = findViewById(R.id.video);
        vol = (SeekBar) findViewById(R.id.vol);
        doubleClick = (Switch) findViewById(R.id.double_click);
        Uri fileURI = FileProvider.getUriForFile(
                getApplicationContext(),
                getApplicationContext()
                        .getPackageName() + ".provider", new File(urlvideo));
        videoView.setVideoURI(fileURI);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Objects.requireNonNull(audioManager).getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        videoView.setOnPreparedListener(mp -> {
            mediaPlayer = mp;
            enableSound(sharedPreferences.getFloat("VOLUMEF", 0.5f));
            mp.setLooping(true);
            mp.start();

        });
        doubleClick.setChecked(sharedPreferences.getBoolean("DOUBLE", true));
        for (int i = 0; i < values.length - 1; i++) {
            if (values[i] == sharedPreferences.getFloat("VOLUMEF", 0.5f)) {
                vol.setProgress(i);
                break;
            }
        }
        vol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                float f = (float) progress / 100;
                enableSound(f);
                Log.e("F", f + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void set(View view) {
        sharedPreferences.edit().putString("VIDEO", urlvideo).apply();
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        try {
            wallpaperManager.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, VideoWallpaperService.class));
        isClick = doubleClick.isChecked();
        sharedPreferences.edit().putBoolean("DOUBLE", doubleClick.isChecked()).apply();
        startActivity(intent);
        finish();
    }


    public void enableSound(float aFloat) {

        sharedPreferences.edit().putFloat("VOLUMEF", aFloat).apply();
        Log.e("checkingsounds", "&&&&&   " + aFloat);
        mediaPlayer.setVolume(aFloat, aFloat);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFeleteFile) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFeleteFile) {
            setResult(RESULT_OK);
        }
    }
}
