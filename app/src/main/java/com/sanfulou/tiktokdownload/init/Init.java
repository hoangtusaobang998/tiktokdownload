package com.sanfulou.tiktokdownload.init;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.sanfulou.tiktokdownload.BuildConfig;
import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.listen.GetListFile;
import com.sanfulou.tiktokdownload.listen.ValidateUrl;
import com.sanfulou.tiktokdownload.model.FileMemory;
import com.sanfulou.tiktokdownload.utils.UtilsToast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Init {


    public static final String NULL_POINT = null;
    private static final String URL_IN = "https://www.tiktok.com/";

    public static void closeKeyboard(Context context) {
        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    public static String getFilename() {
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + "AudioTikTok";
        File storageDir = new File(file_path);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return storageDir.getAbsolutePath();
    }

    public static boolean checkConnection(Context context) throws Exception {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        UtilsToast.toast(context, context.getString(R.string.no_internet));
        return false;
    }

    public static final int P_CODE = 999;
    public static final String[] P = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean hasPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && P != null) {
            for (String permission : P) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFile(File file) {
        Date lastModDate = new Date(file.lastModified());
        return new SimpleDateFormat("dd/MM/yyyy").format(lastModDate);
    }

    public static void getFileByType(Context context, GetListFile listFile) {

        if (!hasPermissions(context)) {
            listFile.hasPemission();
            return;
        }
        String path = getFilename();
        File file = new File(path);
        if (!file.exists()) {
            listFile.listNull();
            return;
        }
        File[] pictures = file.listFiles();
        if (pictures == null || pictures.length == 0) {
            listFile.listNull();
            return;
        }

        Arrays.sort(pictures, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
        for (File file1 : pictures) {
            new TaskFile(context, listFile).execute(file1);
        }

    }

    private static class TaskFile extends AsyncTask<File, Void, FileMemory> {

        @SuppressLint("StaticFieldLeak")
        private Context context;
        private GetListFile listFile;

        TaskFile(Context context, GetListFile listFile) {
            this.context = context;
            this.listFile = listFile;
        }

        @Override
        protected FileMemory doInBackground(File... files) {
            FileMemory fileMemory = new FileMemory();
            fileMemory.setPath(files[0].getPath());
            fileMemory.setName(files[0].getName());
            fileMemory.setDate(getDateFile(files[0]));
            fileMemory.setSize(FileUtil.formatFileSize(files[0].length()));
            fileMemory.setDuration(checkVideoDurationValidation(context, files[0].getPath()));
            return fileMemory;
        }

        @Override
        protected void onPostExecute(FileMemory fileMemory) {
            super.onPostExecute(fileMemory);
            listFile.listFile(fileMemory);
        }
    }

    public static String checkVideoDurationValidation(Context context, String uri) throws NullPointerException {
        Log.d("CommonHandler", "Uri: " + uri);

        Uri fileURI = FileProvider.getUriForFile(
                context.getApplicationContext(),
                context.getApplicationContext()
                        .getPackageName() + ".provider", new File(uri));
        long duration = 0;
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = MediaPlayer.create(context, fileURI);
        } catch (Exception e) {
            Log.e("E", e.toString());
            return "";
        }
        if (mediaPlayer == null) {
            return "";
        }
        duration = mediaPlayer.getDuration();
        Date date = new Date();
        date.setTime(duration);
        return new SimpleDateFormat("mm:ss").format(date);
    }

    public static int getFileSum(Context context) {

        if (!hasPermissions(context)) {

            return 0;
        }
        String path = getFilename();
        File file = new File(path);
        if (!file.exists()) {
            //null

            return 0;
        }
        File[] pictures = file.listFiles();
        if (pictures == null || pictures.length == 0) {
            //null
            return 0;
        }
        return pictures.length;
    }

    public static boolean isFile(String s) {
        String file_path = "";
        file_path = getFilename() + "/" + s;
        File dlCacheFile = new File(file_path);
        Log.e("d", dlCacheFile.getPath());
        if (!dlCacheFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static void openGooglePlay(Context context, String packagename) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packagename)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packagename)));
        }
    }

    public static void openApp(Context context, String packagename) {
        try {
            PackageManager pm = context.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(packagename);
            context.startActivity(launchIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            UtilsToast.toast(context, context.getString(R.string.noapp));
        }
    }


    public static int getVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    private static final String URL_TT = "https://www.tiktok.com/";
    private static final String URL_TT_1 = "https://vt.tiktok.com/";
    private static final String URL_TT_2 = "https://t.tiktok.com/";

    public static void validateTT(String url, ValidateUrl validateUrl) {
        if (url.startsWith(URL_TT) || url.startsWith(URL_TT_1)) {
            validateUrl.onSussce(url);
            return;
        }
        if (url.contains(URL_TT) || url.contains(URL_TT_1)) {
            validateUrl.onSussce(sub(url));
            return;
        }
        if (url.startsWith(URL_TT_2)) {
            validateUrl.onSussce(url);
            return;
        }
        validateUrl.onFal();


    }

    private static String sub(String string) {
        int index = string.indexOf("https");
        Log.e("index", index + "");
        if (index == -1) {
            return "";
        }
        return string.substring(index);
    }

    public static void sendMessenger(File myFile, Context context) {
        try {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
            String type = mime.getMimeTypeFromExtension(ext);
            Intent sharingIntent = new Intent("android.intent.action.SEND");
            sharingIntent.setType(type);
            if (getVersionSDK() > 23) {
                sharingIntent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", myFile));
            } else {
                sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
            }
            context.startActivity(Intent.createChooser(sharingIntent, "Share"));
        } catch (Exception e) {

        }
    }



}
