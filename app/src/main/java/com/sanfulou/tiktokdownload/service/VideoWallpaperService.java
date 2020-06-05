package com.sanfulou.tiktokdownload.service;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class VideoWallpaperService extends WallpaperService {
    protected static int playheadTime = 0;
    private SharedPreferences sharedPreferences;
    private static final String ACTION_PREFIX = VideoWallpaperService.class.getName() + ".";
    private BroadcastReceiver mReceiver = null;
    private Handler handler;
    public static String APP_RUNNING = "";
    public static final String PACKNAMEAPP = "com.tiktok.downloader.wall.picture";
    public static boolean is = false;
    public boolean run = false;


    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }


    class VideoEngine extends Engine {


        private final String TAG = getClass().getSimpleName();
        private MediaPlayer mediaPlayer = null;

        private void sendAction(Context context, String action) {
            Intent intent = new Intent();
            intent.setAction(VideoWallpaperService.ACTION_PREFIX + action);
            context.sendBroadcast(intent);
        }

        private GestureDetector gestureListener;

        private class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                //start intent/application here
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    is=true;
                } else {
                    is=false;
                    mediaPlayer.start();
                }

                return super.onDoubleTap(e);
            }
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            gestureListener.onTouchEvent(event);
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            if (sharedPreferences.getBoolean("DOUBLE", true)) {
                gestureListener = new GestureDetector(getApplicationContext(), new GestureListener());
                setTouchEventsEnabled(true);
            }
        }

        public VideoEngine() {
            super();
            Log.i(TAG, "( VideoEngine )");
            sharedPreferences = getSharedPreferences("DATA", MODE_PRIVATE);
            File file = new File(sharedPreferences.getString("VIDEO", ""));
            Uri fileURI = FileProvider.getUriForFile(
                    getApplicationContext(),
                    getApplicationContext()
                            .getPackageName() + ".provider", file);
            mediaPlayer = MediaPlayer.create(getBaseContext(), fileURI);

        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {

            APP_RUNNING = nameOfHomeApp();
            Log.i(TAG, "onSurfaceCreated");
            mediaPlayer.setSurface(holder.getSurface());
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(sharedPreferences.getFloat("VOLUMEF", 0.5f), sharedPreferences.getFloat("VOLUMEF", 0.5f));
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e("A", mp.getDuration() + "");
                }
            });


        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "( INativeWallpaperEngine ): onSurfaceDestroyed");
            playheadTime = mediaPlayer.getCurrentPosition();
            mediaPlayer.reset();
            mediaPlayer.release();
        }

        private String printForegroundTask() {
            String currentApp = "NULL";
            long time = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                @SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, time);
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    Log.e("SIZE", mySortedMap.size() + "");
                    for (UsageStats usageStats : appList) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                        currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                } else {
                    Log.e("SIZE", "NULL");
                }
            } else {
                @SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
                UsageEvents usageEvent = usm.queryEvents(time - 100 * 1000, time);
                UsageEvents.Event event = new UsageEvents.Event();
                // get last event
                while (usageEvent.hasNextEvent()) {
                    usageEvent.getNextEvent(event);
                }
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentApp = event.getPackageName();
                }
            }

            return currentApp;
        }

        public class Running implements Runnable {

            @Override
            public void run() {
//                for (long i = 0; true; i++) {
//                    if (sharedPreferences.getBoolean("runs", false)) {
//                        return;
//                    }
//                    String apprunning = printForegroundTask();
//                    Log.e("apprunning", apprunning);
//                    Log.e("nameOfHomeApp", nameOfHomeApp());
//                    if (run) {
//                        handler.sendEmptyMessage(4);
//                        return;
//                    }
//                    Log.e("RUN", run + "");
//                    if (i > 10) {
//                        if (nameOfHomeApp().equals(apprunning)) {
//                            handler.sendEmptyMessage(1);
//                        } else {
//                            handler.sendEmptyMessage(2);
//                        }
//                    }
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }

            }
        }

        private String nameOfHomeApp() {
            try {
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.addCategory(Intent.CATEGORY_HOME);
                PackageManager pm = getPackageManager();
                final ResolveInfo mInfo = pm.resolveActivity(i, PackageManager.MATCH_DEFAULT_ONLY);
                return mInfo.activityInfo.packageName;
            } catch (Exception e) {
                return "";
            }

        }

        @Override
        public void onDestroy() {
            super.onDestroy();

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

            if (isPreview()) {
                //Toast.makeText(VideoWallpaperService.this, "Oki", Toast.LENGTH_SHORT).show();
                run = true;
            } else {
//                Toast.makeText(VideoWallpaperService.this, "Null", Toast.LENGTH_SHORT).show();
                if (run == true) {
                    try {
                        Intent i = new Intent(Intent.ACTION_MAIN);
                        i.addCategory(Intent.CATEGORY_HOME);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } catch (Exception e) {

                    }
                    run = false;
                }
            }
            if (visible && !is) {
                //Toast.makeText(VideoWallpaperService.this, "Oki", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();
            } else {
                // Toast.makeText(VideoWallpaperService.this, "FALSE", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
            }
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);


        }

        @Override
        public boolean isPreview() {
            return super.isPreview();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
