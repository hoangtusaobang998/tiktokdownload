package com.sanfulou.tiktokdownload.fragment;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.florent37.viewanimator.ViewAnimator;
import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.base.BaseFragment;
import com.sanfulou.tiktokdownload.download.DownloadFile;
import com.sanfulou.tiktokdownload.init.FileUtil;
import com.sanfulou.tiktokdownload.init.Init;
import com.sanfulou.tiktokdownload.listen.Const;
import com.sanfulou.tiktokdownload.listen.OnDownloadFile;
import com.sanfulou.tiktokdownload.listen.ValidateUrl;
import com.sanfulou.tiktokdownload.model.FileMemory;
import com.sanfulou.tiktokdownload.task.TaskGetUserName;
import com.sanfulou.tiktokdownload.task.TaskUrl;
import com.sanfulou.tiktokdownload.task.TaskUrlTikTok;
import com.sanfulou.tiktokdownload.utils.UtilsClip;
import com.sanfulou.tiktokdownload.utils.UtilsShar;
import com.sanfulou.tiktokdownload.utils.UtilsToast;
import com.sanfulou.tiktokdownload.utils.UtilsView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.Objects;

import static com.sanfulou.tiktokdownload.init.Init.checkVideoDurationValidation;
import static com.sanfulou.tiktokdownload.init.Init.getDateFile;

public class TrangChu extends BaseFragment implements OnDownloadFile, ValidateUrl {

    private EditText edUrl;
    private Button download;
    private LottieAnimationView animIvOn, animIvSuccess;
    private ViewGroup transitionsContainer;
    private TextView tvLoading, tvPro;
    private ImageView imgAudio;
    private ImageView imgVideo;
    private boolean isDown = false;


    @Override
    protected int initLayout() {
        return R.layout.trangchu;
    }

    private void initTransitionsContainer() {
        TransitionManager.beginDelayedTransition(transitionsContainer);
    }

    private void setAnimTvDownload() {
        ViewAnimator.animate(tvLoading).wobble().duration(6000).repeatCount(ViewAnimator.REVERSE).repeatCount(ViewAnimator.INFINITE).onStop(() -> {
        }).start();
    }

    @Override
    protected void initView(View view) {
        Init.closeKeyboard(getContext());
        this.transitionsContainer = view.findViewById(R.id.transitions_container);
        this.edUrl = view.findViewById(R.id.ed_url);
        this.download = view.findViewById(R.id.download);
        this.tvPro = view.findViewById(R.id.tv_pro);
        this.animIvOn = view.findViewById(R.id.anim_iv_downloading);
        this.imgAudio = view.findViewById(R.id.img_audio);
        this.imgVideo = view.findViewById(R.id.type);
        this.animIvSuccess = view.findViewById(R.id.anim_iv_success);
        this.tvLoading = view.findViewById(R.id.tv_loading);
        ImageView logo = view.findViewById(R.id.logo);
        this.download.setOnClickListener(this::click);
        this.imgAudio.setOnClickListener(this::selectionType);
        this.imgVideo.setOnClickListener(this::selectionType);
        logo.setOnClickListener(this::openAppTiktok);
        if (!UtilsShar.gettBSelectType(getContext())) {
            this.setBackgroundViewSelect(this.imgAudio, this.imgVideo);
        } else {
            this.setBackgroundViewSelect(this.imgVideo, this.imgAudio);

        }
    }

    private void click(View v) {
        this.download.setClickable(false);
        String url = edUrl.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            ViewAnimator.animate(edUrl).bounceIn().duration(1000).onStop(() -> download.setClickable(true)).start();
            UtilsToast.toast(v.getContext(), getString(R.string.url_isempty));
            return;
        }
        Init.validateTT(url, TrangChu.this);
    }

    private void selectionType(View v) {
        if (!UtilsShar.gettBSelectType(getContext()) && v.getId() == R.id.type) {
            this.setBackgroundViewSelect(this.imgVideo, this.imgAudio);
            UtilsShar.putBSelectType(getContext(), true);
        }
        if (UtilsShar.gettBSelectType(getContext()) && v.getId() == R.id.img_audio) {
            UtilsShar.putBSelectType(getContext(), false);
            this.setBackgroundViewSelect(this.imgAudio, this.imgVideo);
        }

    }

    private void openAppTiktok(View v) {
        ViewAnimator.animate(v).bounceIn().duration(1000).onStop(() -> Init.openApp(v.getContext(), Const.TIKTOK)).start();
    }

    private void setBackgroundViewSelect(View v1, View v2) {
        v1.setBackgroundResource(R.drawable.shape_tiktok);
        v2.setBackgroundResource(R.drawable.shape_ccc);
        ViewAnimator.animate(v1, v2).bounceIn().duration(1000).onStop(() -> {
        }).start();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSussce(String url) {
        this.initTransitionsContainer();
        try {
            UtilsClip.setClipboardText(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ViewAnimator.animate(download).bounceOut().duration(1000).onStop(() -> UtilsView.setInvisiableView(this.download)).start();
        UtilsView.setVisiable(this.tvLoading);
        UtilsView.setVisiable(this.tvPro);
        this.tvPro.setText("0Mb/0Mb");
        UtilsView.setGoneView(this.animIvOn);
        UtilsView.setVisiable(this.animIvOn);
        this.animIvOn.playAnimation();
        this.setAnimTvDownload();

        if (UtilsShar.gettBSelectType(getContext())) {
            TaskGetUserName.executeData().setUrlAudio(new TaskUrlTikTok.UrlAudio() {
                @Override
                public void onDataUrl(String[] url) {

                    isDown = true;

                    DownloadFile.startDownloadFile(url[1], url[0] + ".mp4", TrangChu.this);
                }

                @Override
                public void onDataUrlError(String messge) {

                    visiableView();
                    UtilsToast.toast(getContext(), getString(R.string.thulai));

                }
            }).connectUrl(url);
            return;
        }
        TaskUrl.executeData().setUrlAudio(new TaskUrl.UrlAudio() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataUrl(String[] url) {
                String urllink = url[1].replaceAll("\\s+", "").replaceAll("[^A-Za-z0-9]", "").replaceAll("\uD83D\uDE0D", "");
                urllink = urllink + ".mp3";
                if (!Init.isFile(urllink)) {
                    UtilsToast.toast(getContext(), getString(R.string.tontai));
                    visiableView();
                    return;
                }
                isDown = true;
                DownloadFile.startDownloadFile(url[0], urllink, TrangChu.this);
            }

            @Override
            public void onDataUrlError(String messge) {
                visiableView();
                UtilsToast.toast(getContext(), getString(R.string.thulai));
            }
        }).connectUrl(url, true);
    }

    @Override
    public void onFal() {
        UtilsToast.toast(getContext(), getString(R.string.nolinks));
        this.edUrl.setText("");
        this.download.setClickable(true);
    }

    @Override
    public void onNull() {

    }

    @Override
    public void onProgress(String txt) {
        this.tvPro.setText(txt);
    }

    @Override
    public void onDownloadComplete(File file) {
        this.visiableView();
        this.edUrl.setText("");
        this.animIvSuccess.setVisibility(View.VISIBLE);
        this.animIvSuccess.playAnimation();
        FileMemory fileMemory = new FileMemory();
        fileMemory.setPath(file.getPath());
        fileMemory.setName(file.getName());
        fileMemory.setDate(getDateFile(file));
        fileMemory.setSize(FileUtil.formatFileSize(file.length()));
        fileMemory.setDuration(checkVideoDurationValidation(Objects.requireNonNull(getContext()), file.getPath()));
        ViewAnimator.animate(animIvSuccess).bounceOut().duration(2000).onStop(() -> {
            UtilsView.setGoneView(animIvSuccess);
            EventBus.getDefault().post(fileMemory);
        }).start();

        this.isDown = false;

    }

    private void visiableView() {
        this.download.setClickable(true);
        UtilsView.setVisiable(this.download);
        ViewAnimator.animate(download).bounceIn().duration(1000).start();
        UtilsView.setGoneView(this.tvLoading);
        UtilsView.setGoneView(this.tvPro);
        UtilsView.setGoneView(this.animIvOn);
        this.animIvOn.pauseAnimation();
    }

    @Override
    public void onError(String messge) {
        Log.e("onError", messge);
        this.visiableView();
        this.isDown = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isDown) {
            return;
        }
        String url = UtilsClip.getClipboardText(getContext());
        if (url != null) {
            if (url.isEmpty()) {
                return;
            }
            this.edUrl.setText(url);
            Init.validateTT(url, TrangChu.this);
        }
    }


}
