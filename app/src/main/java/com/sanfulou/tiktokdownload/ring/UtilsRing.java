package com.sanfulou.tiktokdownload.ring;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.utils.UtilsToast;

import java.io.File;

public class UtilsRing {

    public static final int RINGTONE = RingtoneManager.TYPE_RINGTONE;
    public static final int NOTIFICATION_SOUND = RingtoneManager.TYPE_NOTIFICATION;
    public static final int ALARM = RingtoneManager.TYPE_ALARM;


    public static void setRingTone(Context c, String url, int type) {
        if (!checkSystemWritePermission(c)) {
            return;
        }
        setRingTone(c, FileProvider.getUriForFile(
                c.getApplicationContext(),
                c.getApplicationContext()
                        .getPackageName() + ".provider", new File(url)), type);
    }

    private static void setRingTone(Context context, Uri newUri, int type) {
        try {
            RingtoneManager.setActualDefaultRingtoneUri(context,type , newUri);
            UtilsToast.toast(context, context.getString(R.string.thanhcong));
        } catch (Throwable t) {
            Log.d("TAG", "catch exception");
        }
//        Settings.System.putString(context.getContentResolver(),
//                type, newUri.toString());
    }

    private static boolean checkSystemWritePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context))
                return true;
            else
                openAndroidPermissionsMenu(context);
        }
        return false;
    }

    private static void openAndroidPermissionsMenu(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }
}
