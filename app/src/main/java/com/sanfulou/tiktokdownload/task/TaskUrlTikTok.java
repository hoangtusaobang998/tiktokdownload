package com.sanfulou.tiktokdownload.task;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.sanfulou.tiktokdownload.logutils.LogUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;

public class TaskUrlTikTok extends AsyncTask<String, Void, String[]> {
    private String CSS_QUERY_TYPE = "";
    private static final int TIME_OUT_CONNECT = 20000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String URL_GOOGLE = "http://www.google.com";
    private int cssType = -1;
    private UrlAudio urlAudio;
    public static final int VIDEO = 890;
    public static final int AUDIO = 2;

    public static TaskUrlTikTok executeData() {
        return new TaskUrlTikTok();
    }

    public void connectUrl(String url, int type) {
        if (type == this.VIDEO) {
            this.cssType = this.VIDEO;
            this.CSS_QUERY_TYPE = "video#click_play_mp4";
        } else if (type == this.AUDIO) {
            this.cssType = this.AUDIO;
            this.CSS_QUERY_TYPE = "div.col-md-8.video-links > a#click_pre_download_mp3";
        }
        execute(url);
    }

    public TaskUrlTikTok setUrlAudio(UrlAudio urlAudio) {
        this.urlAudio = urlAudio;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String[] doInBackground(String... strings) {
        Log.e("CSS", this.CSS_QUERY_TYPE);
        String[] mang = new String[2];
        try {
            Connection.Response response = Jsoup.connect(strings[0])
                    .ignoreContentType(true)
                    .userAgent(this.USER_AGENT)
                    .referrer(this.URL_GOOGLE)
                    .timeout(this.TIME_OUT_CONNECT)
                    .followRedirects(true)
                    .execute();

            if (response == null) {
                return null;
            }
            Document doc = response.parse();
            if (doc == null) {
                return null;
            }

            Elements sub = doc.select(this.CSS_QUERY_TYPE);
            if (sub == null) {
                return null;
            }
            if (sub.isEmpty()) {
                return null;
            }

            if (this.cssType == this.VIDEO) {
                if (TextUtils.isEmpty(sub.html())) {
                    return null;
                }
                String html = sub.html();
                String dataSource = html.split(">")[0];
                LogUtils.logE(dataSource);
                String urlVideo = dataSource.replace("<source data-src=\"", "").replace("\" type=\"video/mp4\"", "");
                LogUtils.logE(urlVideo);
                mang[0] = String.valueOf(Calendar.getInstance().getTimeInMillis());
                mang[1] = urlVideo;
                return mang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] url) {
        super.onPostExecute(url);
        if (urlAudio == null) {
            return;
        }
        if (url == null) {
            urlAudio.onDataUrlError("Url null");
            return;
        }
        urlAudio.onDataUrl(url);
    }

    public interface UrlAudio {

        void onDataUrl(String[] url);

        void onDataUrlError(String messge);
    }
}
