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

public class DialogIntro extends AlertDialog {
    protected DialogIntro(Context context) {
        super(context);
    }

    protected DialogIntro(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected DialogIntro(Context context, int themeResId) {
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
        setContentView(R.layout.dialog_introduct);
        ButterKnife.bind(this);
        ViewAnimator.animate(layout).bounceIn().duration(1500).start();
        setOnCancelListener(dialog -> {
            cancel();
        });
    }

    public static DialogIntro initDialogRate(Context context) {

        return new DialogIntro(context);
    }

    @BindView(R.id.layout)
    ConstraintLayout layout;

    @OnClick(R.id.rate)
    public void exitClickView() {
        cancel();
    }

}
