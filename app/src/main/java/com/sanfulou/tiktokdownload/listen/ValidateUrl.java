package com.sanfulou.tiktokdownload.listen;

public interface ValidateUrl {

    void onSussce(String url);

    void onFal();

    void onNull();
}
