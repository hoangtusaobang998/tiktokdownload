package com.sanfulou.tiktokdownload.task;

import android.os.AsyncTask;
import android.util.Log;

import com.sanfulou.tiktokdownload.logutils.LogUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

import static com.sanfulou.tiktokdownload.listen.Const.URL_TIKTOK_VIDEO;

public class TaskGetUserName extends AsyncTask<String, Void, String> {
    private String CSS_QUERY_TYPE = "div.jsx-1038045583.jsx-861547433.jsx-3645511632.user-info-content > h2";
    private static final int TIME_OUT_CONNECT = 20000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    private static final String URL_GOOGLE = "http://www.google.com";
    private int cssType = -1;
    private TaskUrlTikTok.UrlAudio urlAudio;

    public static TaskGetUserName executeData() {
        return new TaskGetUserName();
    }

    public void connectUrl(String url) {
        execute(url);
    }

    public TaskGetUserName setUrlAudio(TaskUrlTikTok.UrlAudio urlAudio) {
        this.urlAudio = urlAudio;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected String doInBackground(String... strings) {
        Log.e("CSS", strings[0]);
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
            String id = doc.baseUri().split("\\/")[5].split("\\?")[0];
            String[] user = sub.get(0).text().split("\\s");
            String username = "";
            for (int i = 0; i < user.length; i++) {
                if (i < user.length - 1) {
                    username += removeAccents(stripAccents(user[i])) + "-";
                }
                if (i == user.length - 1) {
                    username += removeAccents(stripAccents(user[i]));
                }
            }

            String url = URL_TIKTOK_VIDEO + username + "/" + id;
            return url;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        super.onPostExecute(url);
        if (urlAudio == null) {
            return;
        }
        if (url == null) {
            urlAudio.onDataUrlError("Url null");
            return;
        }
        LogUtils.logE(url);
        TaskUrlTikTok.executeData().setUrlAudio(this.urlAudio).connectUrl(url, TaskUrlTikTok.VIDEO);
    }

    private static Map<Character, Character> MAP_NORM;

    public static String removeAccents(String value) {
        if (MAP_NORM == null || MAP_NORM.size() == 0) {
            MAP_NORM = new HashMap<Character, Character>();
            MAP_NORM.put('À', 'A');
            MAP_NORM.put('Á', 'A');
            MAP_NORM.put('Â', 'A');
            MAP_NORM.put('Ã', 'A');
            MAP_NORM.put('Ä', 'A');
            MAP_NORM.put('È', 'E');
            MAP_NORM.put('É', 'E');
            MAP_NORM.put('Ê', 'E');
            MAP_NORM.put('Ë', 'E');
            MAP_NORM.put('Í', 'I');
            MAP_NORM.put('Ì', 'I');
            MAP_NORM.put('Î', 'I');
            MAP_NORM.put('Ï', 'I');
            MAP_NORM.put('Ù', 'U');
            MAP_NORM.put('Ú', 'U');
            MAP_NORM.put('Û', 'U');
            MAP_NORM.put('Ü', 'U');
            MAP_NORM.put('Ò', 'O');
            MAP_NORM.put('Ó', 'O');
            MAP_NORM.put('Ô', 'O');
            MAP_NORM.put('Õ', 'O');
            MAP_NORM.put('Ö', 'O');
            MAP_NORM.put('Ñ', 'N');
            MAP_NORM.put('Ç', 'C');
            MAP_NORM.put('ª', 'A');
            MAP_NORM.put('º', 'O');
            MAP_NORM.put('§', 'S');
            MAP_NORM.put('³', '3');
            MAP_NORM.put('²', '2');
            MAP_NORM.put('¹', '1');
            MAP_NORM.put('à', 'a');
            MAP_NORM.put('á', 'a');
            MAP_NORM.put('â', 'a');
            MAP_NORM.put('ầ', 'a');
            MAP_NORM.put('ấ', 'a');
            MAP_NORM.put('ậ', 'a');
            MAP_NORM.put('ă', 'a');
            MAP_NORM.put('ã', 'a');
            MAP_NORM.put('ä', 'a');
            MAP_NORM.put('è', 'e');
            MAP_NORM.put('é', 'e');
            MAP_NORM.put('ê', 'e');
            MAP_NORM.put('ệ', 'e');
            MAP_NORM.put('ë', 'e');
            MAP_NORM.put('í', 'i');
            MAP_NORM.put('ị', 'i');
            MAP_NORM.put('ì', 'i');
            MAP_NORM.put('î', 'i');
            MAP_NORM.put('ï', 'i');
            MAP_NORM.put('ù', 'u');
            MAP_NORM.put('ú', 'u');
            MAP_NORM.put('û', 'u');
            MAP_NORM.put('ü', 'u');
            MAP_NORM.put('ò', 'o');
            MAP_NORM.put('ó', 'o');
            MAP_NORM.put('ô', 'o');
            MAP_NORM.put('õ', 'o');
            MAP_NORM.put('ö', 'o');
            MAP_NORM.put('ñ', 'n');
            MAP_NORM.put('ç', 'c');
            MAP_NORM.put('Đ', 'd');
            MAP_NORM.put('đ', 'd');
        }

        if (value == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(value);

        for (int i = 0; i < value.length(); i++) {
            Character c = MAP_NORM.get(sb.charAt(i));
            if (c != null) {
                sb.setCharAt(i, c.charValue());
            }
        }

        return sb.toString().replaceAll("\\s+", "").replaceAll("[^A-Za-z0-9]", "").replaceAll("\uD83D\uDE0D", "").toLowerCase();

    }

    public static String stripAccents(String input){
        return input == null ? null :
                Normalizer.normalize(input, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

}
