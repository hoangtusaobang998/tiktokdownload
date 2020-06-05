package com.sanfulou.tiktokdownload.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sanfulou.tiktokdownload.listen.Const;

public class UtilsShar {

    private static SharedPreferences createShar(Context context) {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE);
    }

    public static void putBSelectType(Context context,boolean i) {
        createShar(context).edit().putBoolean(Const.SELECT, i).apply();
    }

    public static boolean gettBSelectType(Context context) {
        return createShar(context).getBoolean(Const.SELECT, false);
    }

}
