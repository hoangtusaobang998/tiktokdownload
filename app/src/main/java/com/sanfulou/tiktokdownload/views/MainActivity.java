package com.sanfulou.tiktokdownload.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.fragment.Download;
import com.sanfulou.tiktokdownload.fragment.TrangChu;
import com.sanfulou.tiktokdownload.init.Init;
import com.sanfulou.tiktokdownload.model.FileMemory;
import com.sanfulou.tiktokdownload.model.GetFile;
import com.sanfulou.tiktokdownload.model.Media;
import com.sanfulou.tiktokdownload.utils.UtilsClip;
import com.sanfulou.tiktokdownload.views.dialog.DialogAds;
import com.sanfulou.tiktokdownload.views.dialog.DialogIntro;
import com.sanfulou.tiktokdownload.views.dialog.DialogRate;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sanfulou.tiktokdownload.init.Init.P;
import static com.sanfulou.tiktokdownload.init.Init.P_CODE;
import static com.sanfulou.tiktokdownload.init.Init.hasPermissions;
import static com.sanfulou.tiktokdownload.init.Init.openGooglePlay;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.tv_home)
    TextView tvHome;

    @BindView(R.id.tv_download)
    TextView tvDownload;

    @BindView(R.id.tv_file)
    TextView tvFile;

    private boolean isDownload = false;
    private SharedPreferences prefer;
    private InterstitialAd mInterstitialAd;
    private boolean rate = false;

    private void initAds() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.inter_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
            }

            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });
    }

    private void showAds() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    @OnClick({R.id.tv_download, R.id.tv_home})
    public void onClick(View v) {
        int position = this.viewPager.getCurrentItem();
        if (position == 0 && v.getId() == R.id.tv_download) {
            this.setTvDownload();
            this.viewPager.setCurrentItem(1);
        }
        if (position == 1 && v.getId() == R.id.tv_home) {
            this.setTvHome();
            this.viewPager.setCurrentItem(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAds();
        prefer = getSharedPreferences("data", MODE_PRIVATE);
        ButterKnife.bind(this);
        this.initViewPager();
        this.initAnimatorView();
        this.getSumFile();
        showDialog();
    }

    private void showDialog() {
        if (prefer.getBoolean("intro", false)) {
            //-> Show Ads
            DialogAds.initDialogRate(this, ads ->
            {
                showAds();
                ads.cancel();
            }).show();
            return;
        }
        DialogIntro.initDialogRate(this).show();
        prefer.edit().putBoolean("intro", true).apply();
    }

    private void getSumFile() {
        try {
            this.tvFile.setText(String.valueOf(Init.getFileSum(this)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initViewPager() {
        this.viewPager.setAdapter(initFragmentAdapterItem());
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setTvHome();
                } else {
                    if (!isDownload) {
                        EventBus.getDefault().post(new GetFile());
                        isDownload = true;
                    }
                    setTvDownload();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.viewPager.setOffscreenPageLimit(2);
    }

    private void setTvHome() {
        this.tvDownload.setAlpha(0.25f);
        ViewAnimator.animate(tvHome).bounceIn().duration(100).start();
    }

    private void setTvDownload() {
        this.tvHome.setAlpha(0.25f);
        ViewAnimator.animate(tvDownload).bounceIn().duration(100).start();
    }

    private void initAnimatorView() {
        ViewAnimator.animate(viewPager).bounceIn().duration(1500).start();
    }

    private FragmentPagerItemAdapter initFragmentAdapterItem() {
        return new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(getString(R.string.trangchu), TrangChu.class)
                .add(getString(R.string.download), Download.class)
                .create());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        if (!hasPermissions(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(P, P_CODE);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!hasPermissions(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(P, P_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().post(new Media());
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FileMemory event) {
        this.getSumFile();
        this.viewPager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String url = UtilsClip.getClipboardText(MainActivity.this);
        if (!TextUtils.isEmpty(url) && this.viewPager.getCurrentItem() != 0) {
            this.viewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (prefer.getBoolean("rate", false)) {
            super.onBackPressed();
            return;
        }
        if (!rate) {
            rate = true;
            DialogRate.initDialogRate(this, new DialogRate.Click() {
                @Override
                public void clickViewRate() {
                    openGooglePlay(MainActivity.this, getPackageName());
                    prefer.edit().putBoolean("rate", true).apply();
                    finish();
                }

                @Override
                public void exit() {
                    finish();
                }
            }).show();
        } else super.onBackPressed();

    }
}
