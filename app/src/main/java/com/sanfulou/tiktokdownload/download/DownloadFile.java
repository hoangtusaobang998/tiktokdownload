package com.sanfulou.tiktokdownload.download;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.sanfulou.tiktokdownload.init.FileUtil;
import com.sanfulou.tiktokdownload.init.Init;
import com.sanfulou.tiktokdownload.listen.OnDownloadFile;

import java.io.File;

public class DownloadFile {


    public static void startDownloadFile(String url, String title, OnDownloadFile onDownloadFile) {
        try {
            downLoadFile(url, title, onDownloadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void downLoadFile(String url, String title, OnDownloadFile onDownloadFile) {
        Log.e("Url", url);
        Log.e("UrlFile", Init.getFilename());
        Log.e("title", title);

        AndroidNetworking.download(url, Init.getFilename(), title)
                .setTag(1).setPriority(Priority.HIGH)
                .build()
                .setDownloadProgressListener((bytesDownloaded, totalBytes) -> {
                    onDownloadFile.onProgress(FileUtil.getProgressDisplayLine(bytesDownloaded, totalBytes));
                    Log.e("AAAAAAAA", "Downloading " + FileUtil.formatFileSize(totalBytes));
                })
                .startDownload(new com.androidnetworking.interfaces.DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        File file = new File(Init.getFilename() + "/" + title);
                        onDownloadFile.onDownloadComplete(file);

                    }

                    @Override
                    public void onError(ANError anError) {
                        onDownloadFile.onError(anError.toString());
                    }
                });
    }
}
