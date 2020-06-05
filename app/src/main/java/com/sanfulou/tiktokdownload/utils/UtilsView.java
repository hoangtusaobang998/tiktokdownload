package com.sanfulou.tiktokdownload.utils;

import android.view.View;

public class UtilsView {

    public static void setGoneView(View v) {
        if (v == null) {
            return;
        }
        v.setVisibility(View.GONE);
    }

    public static void setInvisiableView(View v) {
        if (v == null) {
            return;
        }
        v.setVisibility(View.INVISIBLE);
    }

    public static void setVisiable(View v) {
        if (v == null) {
            return;
        }
        v.setVisibility(View.VISIBLE);
    }
}
