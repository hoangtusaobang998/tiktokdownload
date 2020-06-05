package com.sanfulou.tiktokdownload.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.adapter.AdapterFile;
import com.sanfulou.tiktokdownload.base.BaseFragment;
import com.sanfulou.tiktokdownload.listen.GetListFile;
import com.sanfulou.tiktokdownload.model.FileMemory;
import com.sanfulou.tiktokdownload.model.GetFile;
import com.sanfulou.tiktokdownload.model.Media;
import com.sanfulou.tiktokdownload.model.Scroll;
import com.sanfulou.tiktokdownload.ring.UtilsRing;
import com.sanfulou.tiktokdownload.utils.UtilsToast;
import com.sanfulou.tiktokdownload.utils.UtilsView;
import com.sanfulou.tiktokdownload.views.DetailsVideo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.sanfulou.tiktokdownload.init.Init.P;
import static com.sanfulou.tiktokdownload.init.Init.P_CODE;
import static com.sanfulou.tiktokdownload.init.Init.getFileByType;
import static com.sanfulou.tiktokdownload.init.Init.hasPermissions;
import static com.sanfulou.tiktokdownload.init.Init.sendMessenger;

public class Download extends BaseFragment implements GetListFile {

    private RecyclerView recycer;
    private LinearLayoutManager linearLayoutManager;
    private AdapterFile adapterFile;
    private List<FileMemory> fileMemories;
    private MediaPlayer mediaPlayer;
    int mTrackFinish = 0;
    private ProgressBar progressBar;

    @Override
    protected int initLayout() {
        return R.layout.download;
    }

    @Override
    protected void initView(View view) {
        recycer = view.findViewById(R.id.recycer);
        progressBar = view.findViewById(R.id.pro);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        fileMemories = new ArrayList<>();
        adapterFile = new AdapterFile(fileMemories, getContext());
        recycer.setLayoutManager(linearLayoutManager);
        recycer.setHasFixedSize(true);
        recycer.setAdapter(adapterFile);
        adapterFile.setClick(new AdapterFile.Click() {
            @Override
            public void playAudio(FileMemory fileMemory) {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                    mediaPlayer = MediaPlayer.create(getContext(), FileProvider.getUriForFile(
                            Objects.requireNonNull(getContext()).getApplicationContext(),
                            getContext().getApplicationContext()
                                    .getPackageName() + ".provider", new File(fileMemory.getPath())));
                    if (mediaPlayer != null) {
                        mediaPlayer.setOnCompletionListener(mp -> {
                            adapterFile.setViewMedia();
                        });
                        mediaPlayer.start();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            @Override
            public void pauseAudio() {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    adapterFile.setViewMedia();
                }
            }

            @Override
            public void playVideo(FileMemory fileMemory, int position) {
                Intent intent = new Intent(getContext(), DetailsVideo.class);
                intent.putExtra(DetailsVideo.URL_VIDEO, fileMemory.getPath());
                intent.putExtra(DetailsVideo.POSITION, position);
                startActivityForResult(intent, 989);
            }

            @Override
            public void baoThuc(FileMemory fileMemory) {
                UtilsRing.setRingTone(getContext(), fileMemory.getPath(), UtilsRing.ALARM);
            }

            @Override
            public void chuong(FileMemory fileMemory) {
                UtilsRing.setRingTone(getContext(), fileMemory.getPath(), UtilsRing.RINGTONE);
            }

            @Override
            public void thongBao(FileMemory fileMemory) {
                UtilsRing.setRingTone(getContext(), fileMemory.getPath(), UtilsRing.NOTIFICATION_SOUND);
            }

            @Override
            public void chiaSe(FileMemory fileMemory) {
                sendMessenger(new File(fileMemory.getPath()), getContext());
            }

            @Override
            public void xoaFile(FileMemory fileMemory, int posi) {
                try {
                    File file = new File(fileMemory.getPath());
                    file.delete();
                    adapterFile.deletiFile(posi);
                    UtilsToast.toast(getContext(), getString(R.string.delete_s));
                    if (mediaPlayer != null && adapterFile.getPosi() == posi) {
                        mediaPlayer.pause();
                        adapterFile.setViewMedia();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void position(int position) {
                Log.e("PO", position + "");
                // recycer.smoothScrollToPosition(position);
            }
        });

    }

    private void getFile() {
        getFileByType(getContext(), Download.this);
    }

    @Override
    public void listFile(FileMemory fileInstagrams) {
        if (getActivity() == null) {
            return;
        }
        if (adapterFile == null) {
            return;
        }
        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
            UtilsView.setGoneView(progressBar);
            adapterFile.setFile(fileInstagrams);
        });
    }

    @Override
    public void listNull() {
    }

    @Override
    public void hasPemission() {
        if (!hasPermissions(getContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(P, P_CODE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FileMemory fileMemory) {
        adapterFile.setFile(fileMemory);
        UtilsView.setGoneView(progressBar);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GetFile getFile) {
        adapterFile.clear();
        new Handler().postDelayed(this::getFile, 1500);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Scroll scroll) {
        if (recycer == null) {
            return;
        }
        recycer.smoothScrollToPosition(0);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Media media) {
        if (mediaPlayer != null && adapterFile != null) {
            mediaPlayer.pause();
            adapterFile.setViewMedia();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 989 && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            UtilsToast.toast(getContext(),getString(R.string.delete_s));
            adapterFile.deletiFile(data.getIntExtra(DetailsVideo.POSITION, -1));
        }
    }
}
