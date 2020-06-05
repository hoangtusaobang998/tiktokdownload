package com.sanfulou.tiktokdownload.task;

import android.os.AsyncTask;
import android.util.Log;

import com.sanfulou.tiktokdownload.listen.Const;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Calendar;

public class TaskUrl extends AsyncTask<String, Void, String[]> {
    private static String CSS_QUERY_TYPE = "div.row > div.col > a.btn.btn-success.mr-1.mb-1";
    private static final int TIME_OUT_CONNECT = 10000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String URL_GOOGLE = "http://www.google.com";
    private boolean isUrl = false;
    private UrlAudio urlAudio;

    public static TaskUrl executeData() {
        return new TaskUrl();
    }

    public void connectUrl(String url, boolean type) {
        if (type) {
            CSS_QUERY_TYPE = "div.row > div.col > a.btn.btn-danger.mr-1.mb-1";
        } else {
            CSS_QUERY_TYPE = "div.row > div.col > a.btn.btn-success.mr-1.mb-1";
        }
        this.isUrl = type;
        execute(url);
    }

    public TaskUrl setUrlAudio(UrlAudio urlAudio) {
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
            Connection.Response response = Jsoup.connect(Const.URL + strings[0])
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
            Elements title = null;
            if (isUrl) {
                title = doc.select("h3.text-truncate.mb-3");
            }
            String titlename;
            if (title == null) {
                titlename = String.valueOf(Calendar.getInstance().getTimeInMillis());
            } else {
                titlename = title.text() + Calendar.getInstance().getTimeInMillis();
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
        if (isUrl) {
            urlAudio.onDataUrl(url);
            return;
        }
        Log.e("Url", url[0]);
        TaskUrl.executeData().setUrlAudio(urlAudio).connectUrl(url[0].substring(Const.URL.length()), true);

    }

    public interface UrlAudio {
        void onDataUrl(String[] url);

        void onDataUrlError(String messge);
    }
}
