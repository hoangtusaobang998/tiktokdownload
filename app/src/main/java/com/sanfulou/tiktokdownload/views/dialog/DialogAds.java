package com.sanfulou.tiktokdownload.views.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.florent37.viewanimator.ViewAnimator;
import com.sanfulou.tiktokdownload.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogAds extends AlertDialog {
    protected DialogAds(Context context) {
        super(context);
    }

    protected DialogAds(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected DialogAds(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(null);
        setContentView(R.layout.dialog_ads);
        ButterKnife.bind(this);
        ViewAnimator.animate(layout).bounceIn().duration(1500).start();
        setOnCancelListener(dialog -> {
            cancel();
        });
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    private static Ads ads;

    public static DialogAds initDialogRate(Context context, Ads ads) {
        DialogAds.ads = ads;
        return new DialogAds(context);
    }

    @BindView(R.id.layout)
    ConstraintLayout layout;

    @OnClick(R.id.rate)
    void exitClickView() {
        if (ads == null) {
            cancel();
            return;
        }
        ads.showAds(this);
    }

    public interface Ads {
        void showAds(DialogAds ads);
    }

}
