package com.sanfulou.tiktokdownload.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.florent37.viewanimator.ViewAnimator;
import com.sanfulou.tiktokdownload.R;
import com.sanfulou.tiktokdownload.model.FileMemory;
import com.sanfulou.tiktokdownload.model.Scroll;
import com.sanfulou.tiktokdownload.utils.UtilsView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

public class AdapterFile extends RecyclerView.Adapter<AdapterFile.HolderFile> {

    private List<FileMemory> fileList;
    private Click click;
    private View v, v1;
    private int posi;

    public int getPosi() {
        return posi;
    }

    public void setView() {
        if (v == null) {
            return;
        } else ((ImageView) v).setImageResource(R.drawable.ic_play_arrow);
        if (v1 != null) {
            UtilsView.setGoneView(v1);
        }
    }

    public void setViewMedia() {
        if (v == null) {
            return;
        } else {
            ((ImageView) v).setImageResource(R.drawable.ic_play_arrow);
            v = null;
            posi = -1;
            if (v1 != null) {
                UtilsView.setGoneView(v1);
                v1 = null;
            }
        }
    }

    public void deletiFile(int position) {
        if (position == -1) {
            return;
        }
        fileList.remove(position);
        notifyDataSetChanged();
    }

    public void setClick(Click click) {
        this.click = click;
    }

    public List<FileMemory> getFileList() {
        return fileList;
    }

    public void clear() {
        fileList.clear();
        notifyDataSetChanged();
    }

    private Context context;

    public AdapterFile(List<FileMemory> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    public void setFile(FileMemory file) {
        fileList.add(0, file);
        notifyItemInserted(0);
        EventBus.getDefault().post(new Scroll());
    }

    @NonNull
    @Override
    public HolderFile onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new HolderFile(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HolderFile holder, int position) {
        ViewAnimator.animate(holder.itemView).bounceIn().duration(1500).start();
        ViewAnimator.animate(holder.row).rollIn().duration(1000).start();
        FileMemory fileMemory = fileList.get(position);
        holder.tvDate.setText(!TextUtils.isEmpty(fileMemory.getDate()) ? fileMemory.getDate() : "");
        holder.tvSize.setText(!TextUtils.isEmpty(fileMemory.getSize()) ? fileMemory.getSize() : "");
        holder.tvTitle.setText(!TextUtils.isEmpty(fileMemory.getName()) ? fileMemory.getName() : "");
        holder.tvDuration.setText(!TextUtils.isEmpty(fileMemory.getDuration()) ? fileMemory.getDuration() : "00:00");

        if (this.v == holder.row && posi == position) {
            UtilsView.setVisiable(holder.anim_iv_audio);
            holder.row.setImageResource(R.drawable.ic_pause);
        } else {
            UtilsView.setGoneView(holder.anim_iv_audio);
            holder.row.setImageResource(R.drawable.ic_play_arrow);
        }

        if (fileMemory.getName().endsWith(".mp4")) {
            holder.row.setOnClickListener(v -> {
                click.playVideo(fileMemory, position);
                click.pauseAudio();
            });
            holder.viewA.setOnClickListener(null);
            UtilsView.setGoneView(holder.viewB);
            holder.img_video.setText("MP4");
        } else {
            holder.img_video.setText("MP3");
            holder.viewA.setOnClickListener(v -> {
                if (holder.viewB.getVisibility() == View.GONE) {
                    ViewAnimator.animate(holder.viewA).bounceIn().duration(1500).start();
                    TransitionManager.beginDelayedTransition(holder.transition);
                    UtilsView.setVisiable(holder.viewB);
                    ViewAnimator.animate(holder.viewB).bounceIn().duration(1500).start();
                    click.position(position);
                    return;
                }
                TransitionManager.beginDelayedTransition(holder.transition);
                ViewAnimator.animate(holder.viewB).bounceOut().duration(1500).onStop(() -> UtilsView.setGoneView(holder.viewB)).start();
            });
            holder.row.setOnClickListener(v -> {
                setView();
                if (this.v != null && this.v == v) {
                    click.pauseAudio();
                    this.v = null;
                    this.v1 = null;
                    this.posi = -1;
                    UtilsView.setGoneView(holder.anim_iv_audio);
                    return;
                }
                UtilsView.setVisiable(holder.anim_iv_audio);
                this.v = v;
                this.v1 = holder.anim_iv_audio;
                this.posi = position;
                holder.row.setImageResource(R.drawable.ic_pause);
                click.playAudio(fileMemory);
            });
        }

        try {
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.fromFile(new File(fileMemory.getPath()))).listener(new RequestListener<Bitmap>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.img);
        } catch (Exception e) {
            Log.e("NullPoin", e.toString());
        }

        holder.delete.setOnClickListener(v -> {
            anim(v);
            click.xoaFile(fileMemory, position);
        });

        holder.alarm.setOnClickListener(v -> {
            anim(v);
            click.baoThuc(fileMemory);
        });

        holder.notification.setOnClickListener(v -> {
            anim(v);
            click.thongBao(fileMemory);
        });

        holder.ring.setOnClickListener(v -> {
            anim(v);
            click.chuong(fileMemory);
        });

        holder.share.setOnClickListener(v -> {
            anim(v);
            click.chiaSe(fileMemory);
        });


    }

    private void anim(View v) {
        ViewAnimator.animate(v).bounceIn().duration(1000).start();
    }


    @Override
    public int getItemCount() {
        if (fileList == null) {
            return 0;
        }
        return fileList.size();
    }

    class HolderFile extends RecyclerView.ViewHolder {
        private ImageView img, row;
        private TextView tvTitle;
        private TextView tvDate;
        private TextView tvSize;
        private TextView tvDuration;
        private CardView transition;
        private ConstraintLayout viewA;
        private ConstraintLayout viewB;
        private ViewGroup viewGroup;
        private ImageView ring;
        private ImageView notification;
        private ImageView alarm;
        private ImageView share;
        private ImageView delete;
        private LottieAnimationView anim_iv_audio;
        private TextView img_video;

        @SuppressLint("CutPasteId")
        HolderFile(@NonNull View itemView) {
            super(itemView);
            viewGroup = itemView.findViewById(R.id.grond);
            img = itemView.findViewById(R.id.img);
            row = itemView.findViewById(R.id.row);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            transition = (CardView) itemView.findViewById(R.id.grond);
            viewA = (ConstraintLayout) itemView.findViewById(R.id.view_a);
            viewB = (ConstraintLayout) itemView.findViewById(R.id.view_b);
            ring = (ImageView) itemView.findViewById(R.id.ring);
            notification = (ImageView) itemView.findViewById(R.id.notification);
            alarm = (ImageView) itemView.findViewById(R.id.alarm);
            share = (ImageView) itemView.findViewById(R.id.share);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            img_video = (TextView) itemView.findViewById(R.id.type);
            anim_iv_audio = (LottieAnimationView) itemView.findViewById(R.id.anim_iv_audio);

        }
    }

    public interface Click {
        void playAudio(FileMemory fileMemory);

        void pauseAudio();

        void playVideo(FileMemory fileMemory, int position);

        void baoThuc(FileMemory fileMemory);

        void chuong(FileMemory fileMemory);

        void thongBao(FileMemory fileMemory);

        void chiaSe(FileMemory fileMemory);

        void xoaFile(FileMemory fileMemory, int posi);

        void position(int position);
    }
}
