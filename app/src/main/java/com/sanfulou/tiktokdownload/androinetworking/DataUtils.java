package com.sanfulou.tiktokdownload.androinetworking;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;

public class DataUtils {

    private Connect connect;
    public static String API_DEFAUT = "http://173.208.244.130:21502/tiktok/";

    public static DataUtils addListent(Connect connect) {
        DataUtils dataUtils = new DataUtils();
        dataUtils.connect = connect;
        return dataUtils;
    }

    public void getUrl(String API, String url) {
        connect.onStart();
        AndroidNetworking.get(API)
                .addQueryParameter("link", url)
                .setTag("TAG")
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Data.class, new ParsedRequestListener<Data>() {
                    @Override
                    public void onResponse(Data data) {
                        Log.e("Data", data.toString());
                        connect.onResponse(data);
                        connect.onStop();
                    }

                    @Override
                    public void onError(ANError anError) {
                        connect.onError(anError);
                        connect.onStop();
                    }
                });
    }

    public void getUrl(String url) {
        connect.onStart();
        AndroidNetworking.get(API_DEFAUT)
                .addQueryParameter("link", url)
                .setTag("TAG")
                .setPriority(Priority.LOW)
                .build()
                .getAsObject(Data.class, new ParsedRequestListener<Data>() {
                    @Override
                    public void onResponse(Data data) {
                        Log.e("Data", data.toString());
                        connect.onResponse(data);
                        connect.onStop();
                    }

                    @Override
                    public void onError(ANError anError) {
                        connect.onError(anError);
                        connect.onStop();
                    }
                });
    }

    public interface Connect {

        void onStart();

        void onResponse(Data data);

        void onError(ANError anError);

        void onStop();
    }

}
