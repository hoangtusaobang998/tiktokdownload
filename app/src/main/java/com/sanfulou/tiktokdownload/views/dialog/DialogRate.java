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

public class DialogRate extends AlertDialog {
    protected DialogRate(Context context) {
        super(context);
    }

    protected DialogRate(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected DialogRate(Context context, int themeResId) {
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
        setContentView(R.layout.dialog_rate);
        ButterKnife.bind(this);
        ViewAnimator.animate(layout).bounceIn().duration(1500).start();
        setOnCancelListener(dialog -> {
            if (click != null) {
                click.exit();
            }
        });

    }

    private static Click click;


    public static DialogRate initDialogRate(Context context, Click click) {
        DialogRate.click = click;
        return new DialogRate(context);
    }

    @BindView(R.id.layout)
    ConstraintLayout layout;

    @OnClick(R.id.rate)
    public void rateClickView() {
        if (click == null) {
            return;
        }
        click.clickViewRate();
    }

    public interface Click {
        void clickViewRate();

        void exit();
    }
}
