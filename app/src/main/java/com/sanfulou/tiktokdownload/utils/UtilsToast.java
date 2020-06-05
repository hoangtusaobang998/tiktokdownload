package com.sanfulou.tiktokdownload.utils;

import android.content.Context;
import android.widget.Toast;

public class UtilsToast {

    public static void toast(Context context, String txt) {
        if (context == null) {
            return;
        }
        Toast.makeText(context, txt, Toast.LENGTH_SHORT).show();
    }

}
