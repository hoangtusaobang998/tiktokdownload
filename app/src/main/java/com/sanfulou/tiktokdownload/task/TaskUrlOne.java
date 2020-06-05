package com.sanfulou.tiktokdownload.task;

import android.os.AsyncTask;
import android.util.Log;

import com.sanfulou.tiktokdownload.listen.Const;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;

public class TaskUrlOne extends AsyncTask<String, Void, String[]> {
    private static String CSS_QUERY_TYPE = "div";
    private static final int TIME_OUT_CONNECT = 15000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String URL_GOOGLE = "http://www.google.com";
    private boolean isUrl = false;
    private TaskUrl.UrlAudio urlAudio;

    public static TaskUrlOne executeData() {
        return new TaskUrlOne();
    }

    public void connectUrl(String url) {
        execute(url);
    }

    public TaskUrlOne setUrlAudio(TaskUrl.UrlAudio urlAudio) {
        this.urlAudio = urlAudio;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String[] doInBackground(String... strings) {
        Log.e("CSS", CSS_QUERY_TYPE);
        String[] mang = new String[2];
        try {
            Connection.Response response = Jsoup.connect(Const.URL_1 + strings[0])
                    .ignoreContentType(true)
                    .userAgent(USER_AGENT)
                    .referrer(URL_GOOGLE)
                    .timeout(TIME_OUT_CONNECT)
                    .followRedirects(true)
                    .execute();

            if (response == null) {
                return null;
            }
            Document doc = response.parse();
            if (doc == null) {
                return null;
            }

            Elements sub = doc.select(CSS_QUERY_TYPE);
            if (sub == null) {
                return null;
            }
            if (sub.isEmpty()) {
                return null;
            }
            Log.e("Sub",sub.html());
            Elements title = null;
            if (isUrl) {
                title = doc.select("div.night-dark.night-white.text-black-light.bg-grey-lighter.px-6.py-4.shadow.leading-normal");
            }
            String titlename;
            if (title == null) {
                titlename = String.valueOf(Calendar.getInstance().getTimeInMillis());
            } else {
                titlename = title.text();
            }
            mang[0] = sub.attr("href");
            mang[1] = titlename;
            return mang;
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
//        if (isUrl) {
//            urlAudio.onDataUrl(url);
//            return;
//        }
        urlAudio.onDataUrl(url);
        Log.e("Url", url[0]);
        //TaskUrl.executeData().setUrlAudio(urlAudio).connectUrl(url[0].substring(Const.URL.length()), true);

    }
}
